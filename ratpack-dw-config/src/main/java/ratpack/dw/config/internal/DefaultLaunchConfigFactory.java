package ratpack.dw.config.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.validation.PortRange;
import ratpack.dw.config.Configuration;
import ratpack.dw.config.ConfiguredHandlerFactory;
import ratpack.dw.config.LaunchConfigFactory;
import ratpack.launch.HandlerFactory;
import ratpack.launch.LaunchConfig;
import ratpack.launch.LaunchConfigBuilder;
import ratpack.launch.LaunchException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.net.URI;

public class DefaultLaunchConfigFactory implements LaunchConfigFactory {

  private File baseDir = new File(System.getProperty("user.dir"));
  @PortRange
  private int port = LaunchConfig.DEFAULT_PORT;

  private boolean development;
  @Min(1)
  private int threads = LaunchConfig.DEFAULT_THREADS;
  private URI publicAddress;
  // TODO: support additional properties

  @JsonProperty("handlerFactory")
  @NotNull
  Class<ConfiguredHandlerFactory<?>> handlerFactoryClass;

  public Class<ConfiguredHandlerFactory<?>> getHandlerFactoryClass() {
    return handlerFactoryClass;
  }

  public void setHandlerFactoryClass(Class<ConfiguredHandlerFactory<?>> handlerFactoryClass) {
    this.handlerFactoryClass = handlerFactoryClass;
  }

  public File getBaseDir() {
    return baseDir;
  }

  public void setBaseDir(File baseDir) {
    this.baseDir = baseDir;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public boolean isDevelopment() {
    return development;
  }

  public void setDevelopment(boolean development) {
    this.development = development;
  }

  public int getThreads() {
    return threads;
  }

  public void setThreads(int threads) {
    this.threads = threads;
  }

  public URI getPublicAddress() {
    return publicAddress;
  }

  public void setPublicAddress(URI publicAddress) {
    this.publicAddress = publicAddress;
  }

  @Override
  public LaunchConfig build(Configuration config) {
    ConfiguredHandlerFactory<?> configuredHandlerFactory;
    try {
      configuredHandlerFactory = handlerFactoryClass.newInstance();
    } catch (Exception ex) {
      throw new LaunchException("Could not instantiate configured handler factory: " + handlerFactoryClass.getName(), ex);
    }
    @SuppressWarnings("unchecked") HandlerFactory handlerFactory = new ConfiguredHandlerFactoryAdapter<>(config, (ConfiguredHandlerFactory<Configuration>) configuredHandlerFactory);
    return LaunchConfigBuilder.baseDir(baseDir).port(port).development(development).threads(threads).publicAddress(publicAddress).build(handlerFactory);
  }

}
