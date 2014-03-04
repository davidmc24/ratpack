import geb.buildadapter.BuildAdapterFactory
import geb.driver.SauceLabsDriverFactory
import org.openqa.selenium.firefox.FirefoxDriver

if (!BuildAdapterFactory.getBuildAdapter(this.class.classLoader).reportsDir) {
  reportsDir = "build/geb"
}

driver = {
  def sauceBrowser = System.getProperty("geb.sauce.browser")
  if (sauceBrowser) {
    def username = System.getenv("GEB_SAUCE_LABS_USER")
    assert username
    def accessKey = System.getenv("GEB_SAUCE_LABS_ACCESS_PASSWORD")
    assert accessKey
    new SauceLabsDriverFactory().create(sauceBrowser, username, accessKey)
  } else {
    new FirefoxDriver()
  }
}
