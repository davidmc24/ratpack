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

package ratpack.openid

import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import ratpack.groovy.test.embed.ClosureBackedEmbeddedApplication
import ratpack.openid.provider.KnownProviderDiscoveryUrls
import ratpack.openid.provider.google.GoogleAttribute
import ratpack.session.SessionModule
import ratpack.session.store.MapSessionsModule
import ratpack.test.embed.BaseDirBuilder

class RatpackOpenIdUnderTest extends ClosureBackedEmbeddedApplication {
  RatpackOpenIdUnderTest(int providerPort, int consumerPort, BaseDirBuilder baseDirBuilder) {
    super(baseDirBuilder)
    launchConfig {
      port(consumerPort)
      publicAddress(new URI("http://localhost:${consumerPort}"))
    }
    modules {
      register new SessionModule()
      register new MapSessionsModule(10, 5)
      register new OpenIdRpModule()
      register new AbstractModule() {
        @Override
        protected void configure() {
          bind(ProviderSelectionStrategy).toInstance(new SingleProviderSelectionStrategy("http://localhost:${providerPort}/discovery"))
          Multibinder.newSetBinder(binder(), AuthenticationRequirement).addBinding().toInstance(AuthenticationRequirement.of("/auth"))
          def requiredOpenidAttributeBinder = Multibinder.newSetBinder(binder(), Attribute, Required)
          GoogleAttribute.values().each {requiredOpenidAttributeBinder.addBinding().toInstance(it)}
        }
      }
    }
    handlers {
      get("noauth") {
        response.send "noauth:${SecurityUtils.getUser(context)?.attributes?.get(GoogleAttribute.email.name())}"
      }
      get("auth") {
        response.send "auth:${SecurityUtils.getUser(context)?.attributes?.get(GoogleAttribute.email.name())}"
      }
      get("error") {
        response.send "An error was encountered."
      }
    }
  }
}
