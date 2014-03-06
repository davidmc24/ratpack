package ratpack.openid.pages

import geb.Page

class ErrorPage extends Page {
  static url = "/error"

  static at = {
    driver.currentUrl.contains(url)
  }

  static content = {
    body { $("body") }
  }
}
