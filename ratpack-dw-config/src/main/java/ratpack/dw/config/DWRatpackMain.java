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

package ratpack.dw.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.util.Generics;
import io.dropwizard.validation.valuehandling.OptionalValidatedValueUnwrapper;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.launch.LaunchConfig;
import ratpack.server.RatpackServer;
import ratpack.server.RatpackServerBuilder;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.io.IOException;

public abstract class DWRatpackMain<C extends Configuration> {
  // TODO: figure out the right way to inject an alternate config style into RatpackMain

  private static final Logger LOGGER = LoggerFactory.getLogger(DWRatpackMain.class);
  private static final String PROPERTY_PREFIX = "ratpack.dw";

  public final void run(String[] arguments) {
    File configFile = new File(arguments.length > 0 ? arguments[0] : "ratpack.yaml");
    try {
      Class<C> configurationClass = Generics.getTypeParameter(getClass(), Configuration.class);
      Configuration config = buildConfiguration(configFile, configurationClass);
      LaunchConfigFactory launchConfigFactory = config.getLaunchConfigFactory();
      LaunchConfig launchConfig = launchConfigFactory.build(config);
      RatpackServer server = RatpackServerBuilder.build(launchConfig);
      server.start();
    } catch (Exception e) {
      LOGGER.error("", e);
      System.exit(1);
    }
  }

  private C buildConfiguration(File file, Class<C> configurationClass) throws IOException, ConfigurationException {
    ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure().addValidatedValueHandler(new OptionalValidatedValueUnwrapper()).buildValidatorFactory();
    Validator validator = validatorFactory.getValidator();
    ObjectMapper objectMapper = Jackson.newObjectMapper();
    ConfigurationFactoryFactory<C> factoryFactory = new DefaultConfigurationFactoryFactory<>();
    ConfigurationFactory<C> factory = factoryFactory.create(configurationClass, validator, objectMapper, PROPERTY_PREFIX);
    return factory.build(file);
  }
}
