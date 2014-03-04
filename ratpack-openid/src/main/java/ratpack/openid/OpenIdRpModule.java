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

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import org.openid4java.consumer.ConsumerManager;
import ratpack.guice.HandlerDecoratingModule;
import ratpack.handling.Handler;
import ratpack.handling.Handlers;

import java.util.LinkedList;
import java.util.List;

// TODO: document
public class OpenIdRpModule extends AbstractModule implements HandlerDecoratingModule {
  private static final String DEFAULT_VERIFICATION_PATH = "ratpack.openid/verification";

  private final ProviderSelectionStrategy providerSelectionStrategy;
  private final String verificationPath;

  public OpenIdRpModule() {
    this((ProviderSelectionStrategy) null);
  }

  public OpenIdRpModule(String providerUrl) {
    this(new SingleProviderSelectionStrategy(providerUrl));
  }

  public OpenIdRpModule(ProviderSelectionStrategy providerSelectionStrategy) {
    this(providerSelectionStrategy, DEFAULT_VERIFICATION_PATH);
  }

  public OpenIdRpModule(ProviderSelectionStrategy providerSelectionStrategy, String verificationPath) {
    this.providerSelectionStrategy = providerSelectionStrategy;
    this.verificationPath = verificationPath;
  }

  @Override
  protected void configure() {
    bindConstant().annotatedWith(VerificationPath.class).to(verificationPath);
    Multibinder.newSetBinder(binder(), AuthenticationRequirement.class);
    Multibinder.newSetBinder(binder(), Attribute.class, Required.class);
    Multibinder.newSetBinder(binder(), Attribute.class, Optional.class);
    bind(ConsumerManager.class).in(Scopes.SINGLETON);
    bind(AuthHandler.class);
    bind(CallbackHandler.class);
    if (providerSelectionStrategy != null) {
      bind(ProviderSelectionStrategy.class).toInstance(providerSelectionStrategy);
    }
  }

  @Override
  public Handler decorate(Injector injector, Handler handler) {
    List<Handler> handlers = new LinkedList<>();
    ProviderSelectionStrategy providerSelectionStrategy = injector.getInstance(ProviderSelectionStrategy.class);
    Handler providerSelectionHandler = providerSelectionStrategy.getHandler();
    if (providerSelectionHandler != null) {
      handlers.add(providerSelectionHandler);
    }
    handlers.add(injector.getInstance(CallbackHandler.class));
    handlers.add(injector.getInstance(AuthHandler.class));
    handlers.add(handler);
    return Handlers.chain(handlers.toArray(new Handler[handlers.size()]));
  }
}
