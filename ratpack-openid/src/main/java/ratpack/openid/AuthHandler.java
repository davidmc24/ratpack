/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.openid;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.MessageException;
import org.openid4java.message.ax.FetchRequest;
import ratpack.func.Action;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.handling.Redirector;
import ratpack.http.Request;
import ratpack.server.PublicAddress;
import ratpack.session.store.SessionStorage;

import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.Callable;

import static ratpack.openid.SessionConstants.DISCOVERY_INFO;
import static ratpack.openid.SessionConstants.SAVED_URI;

class AuthHandler implements Handler {
  @Inject
  @VerificationPath
  String verificationPath;

  @Inject
  ProviderSelectionStrategy providerSelectionStrategy;

  @Inject
  Set<AuthenticationRequirement> authenticationRequirements;

  @Inject
  @Required
  Set<Attribute> requiredAttributes;

  @Inject
  @Optional
  Set<Attribute> optionalAttributes;

  @Inject
  ConsumerManager manager;

  @Override
  public void handle(final Context context) throws Exception {
    if (requiresAuthentication(context)) {
      if (SecurityUtils.isAuthenticated(context)) {
        context.next();
      } else {
        if (providerSelectionStrategy.handleProviderSelection(context)) {
          context.getBackground()
            .exec(new DiscoverProviderCallable())
            .then(new ProcessDiscoveryInfoAction(context));
        }
      }
    } else {
      context.next();
    }
  }

  private FetchRequest createFetchRequest() throws MessageException {
    FetchRequest fetchRequest = FetchRequest.createFetchRequest();
    for (Attribute attribute : requiredAttributes) {
      attribute.register(fetchRequest, true);
    }
    for (Attribute attribute : optionalAttributes) {
      attribute.register(fetchRequest, false);
    }
    return fetchRequest;
  }

  private boolean requiresAuthentication(Context context) {
    for (AuthenticationRequirement authenticationRequirement : authenticationRequirements) {
      if (authenticationRequirement.matches(context)) {
        return true;
      }
    }
    return false;
  }

  private class DiscoverProviderCallable implements Callable<DiscoveryInformation> {
    @Override
    public DiscoveryInformation call() throws Exception {
      String discoveryUrl = providerSelectionStrategy.getProviderDiscoveryUrl();
      return manager.associate(manager.discover(discoveryUrl));
    }
  }

  private class ProcessDiscoveryInfoAction implements Action<DiscoveryInformation> {
    private final Context context;

    ProcessDiscoveryInfoAction(Context context) {
      this.context = context;
    }

    @Override
    public void execute(final DiscoveryInformation discoveryInfo) throws Exception {
      context.get(SessionStorage.class).put(DISCOVERY_INFO, discoveryInfo);
      context.getBackground()
        .exec(new AuthenticateCallable(context, discoveryInfo))
        .then(new ProcessAuthenticationAction(context));
    }
  }

  private class AuthenticateCallable implements Callable<AuthRequest> {
    private final Context context;
    private final DiscoveryInformation discoveryInfo;

    AuthenticateCallable(Context context, DiscoveryInformation discoveryInfo) {
      this.context = context;
      this.discoveryInfo = discoveryInfo;
    }

    @Override
    public AuthRequest call() throws Exception {
      String realm = context.get(PublicAddress.class).getAddress(context).toString();
      String returnToUrl = realm + "/" + verificationPath;
      return manager.authenticate(discoveryInfo, returnToUrl, realm);
    }
  }

  private class ProcessAuthenticationAction implements Action<AuthRequest> {
    private final Context context;

    ProcessAuthenticationAction(Context context) {
      this.context = context;
    }

    @Override
    public void execute(AuthRequest authReq) throws Exception {
      Redirector redirector = context.get(Redirector.class);
      Request request = context.getRequest();
      SessionStorage sessionStorage = context.get(SessionStorage.class);
      String requestedUri = request.getUri();
      sessionStorage.put(SAVED_URI, requestedUri);
      authReq.addExtension(createFetchRequest());
      redirector.redirect(context, authReq.getDestinationUrl(true), HttpResponseStatus.FOUND.code());
    }
  }
}
