package ratpack.openid

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler
import org.openid4java.message.ParameterList

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class EmbeddedProvider implements Closeable {
  Queue<Map> results = new LinkedList<>()
  Server server
  PatchedSampleServer sampleServer

  void addResult(Boolean authenticatedAndApproved, String email) {
    results << [authenticatedAndApproved:authenticatedAndApproved, email:email]
  }

  void open(int port) {
    sampleServer = new PatchedSampleServer("http://localhost:${port}/openid_provider/provider/server/o2") {
      @Override
      protected List userInteraction(ParameterList request) {
        def result = results.remove()
        def userSelectedClaimedId = request.getParameterValue("openid.claimed_id")
        return [userSelectedClaimedId, result.authenticatedAndApproved, result.email]
      }
    }
    server = new Server(port)
    server.handler = new AbstractHandler() {
      @Override
      void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        if (request.pathInfo == "/discovery") {
          processDiscovery(response)
        } else {
          sendResponse(sampleServer.processRequest(request, response), response)
        }
      }

      String processDiscovery(HttpServletResponse response) {
        response.setStatus(200)
        response.addHeader("Content-Type", "application/xrds+xml")
        response.outputStream.withWriter {
          it.write("""\
<?xml version="1.0" encoding="UTF-8"?>
<xrds:XRDS xmlns:xrds="xri://\$xrds" xmlns:openid="http://openid.net/xmlns/1.0" xmlns="xri://\$xrd*(\$v*2.0)">
  <XRD version="2.0">
    <Service priority="10">
      <Type>http://specs.openid.net/auth/2.0/signon</Type>
      <Type>http://openid.net/sreg/1.0</Type>
      <Type>http://openid.net/extensions/sreg/1.1</Type>
      <Type>http://schemas.openid.net/pape/policies/2007/06/phishing-resistant</Type>
      <Type>http://openid.net/srv/ax/1.0</Type>
      <URI>${sampleServer.manager.OPEndpointUrl}</URI>
    </Service>
  </XRD>
</xrds:XRDS>"""
          )
        }
      }

      static void sendResponse(String content, HttpServletResponse response) {
        if (content.startsWith("http://")) {
          response.sendRedirect(content)
        } else {
          response.outputStream.withWriter { it.write(content) }
        }
      }
    }
    server.start()
  }

  @Override
  void close() {
    server.stop()
  }
}
