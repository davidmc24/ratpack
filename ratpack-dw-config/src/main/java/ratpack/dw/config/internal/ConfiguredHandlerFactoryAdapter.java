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

package ratpack.dw.config.internal;

import ratpack.dw.config.Configuration;
import ratpack.dw.config.ConfiguredHandlerFactory;
import ratpack.handling.Handler;
import ratpack.launch.HandlerFactory;
import ratpack.launch.LaunchConfig;

public class ConfiguredHandlerFactoryAdapter<C extends Configuration> implements HandlerFactory {
  private final C configuration;
  private final ConfiguredHandlerFactory<C> configuredHandlerFactory;

  public ConfiguredHandlerFactoryAdapter(C configuration, ConfiguredHandlerFactory<C> configuredHandlerFactory) {
    this.configuration = configuration;
    this.configuredHandlerFactory = configuredHandlerFactory;
  }

  @Override
  public Handler create(LaunchConfig launchConfig) throws Exception {
    return configuredHandlerFactory.create(configuration, launchConfig);
  }
}
