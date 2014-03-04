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

import geb.spock.GebReportingSpec
import org.junit.Assume
import org.junit.ClassRule
import org.junit.rules.TemporaryFolder
import ratpack.openid.pages.AuthPage
import ratpack.openid.pages.GoogleAuthorizationPage
import ratpack.openid.pages.GoogleLoginPage
import ratpack.openid.pages.NoAuthPage
import ratpack.test.embed.BaseDirBuilder
import ratpack.test.embed.PathBaseDirBuilder
import spock.lang.AutoCleanup
import spock.lang.Shared

/**
 * Tests OpenID Relying Party support.  To run this test you will need a set of Google credentials.
 * Pass them to the test via the "OPENID_GOOGLE_EMAIL" and "OPENID_GOOGLE_PASSWORD" environment variables.
 * Then, either install FireFox and run the "browserTest" target or configure the "GEB_SAUCE_LABS_USER" and
 * "GEB_SAUCE_LABS_ACCESS_PASSWORD" environment variables and run the "allSauceTests" target.
 */
class OpenIdRpSpec extends GebReportingSpec {
  @Shared
  @ClassRule
  TemporaryFolder temporaryFolder

  @Shared
  @AutoCleanup
  BaseDirBuilder baseDir

  @Shared
  RatpackOpenIdUnderTest aut

  @Shared
  String email
  @Shared
  String password

  def setupSpec() {
    email = System.getenv("OPENID_GOOGLE_EMAIL")
    password = System.getenv("OPENID_GOOGLE_PASSWORD")
    Assume.assumeTrue(email && password)
    baseDir = new PathBaseDirBuilder(temporaryFolder.newFolder("app"))
    aut = new RatpackOpenIdUnderTest(baseDir)
  }

  def setup() {
    browser.baseUrl = aut.address.toString()
  }

  def cleanup() {
    if (browser) {
      browser.clearCookies()
    }
  }

  def cleanupSpec() {
    if (aut) {
      aut.close()
    }
  }

  def "test noauth"() {
    when:
    to NoAuthPage

    then:
    body.text() == "noauth:null"
  }

  def "test google auth"() {
    when:
    go AuthPage.url

    then:
    at GoogleLoginPage

    when:
    login(email, password)
    if (at(GoogleAuthorizationPage)) {
      accept()
    }

    then:
    at AuthPage
    body.text() == "auth:${email}"

    when:
    to NoAuthPage

    then:
    at NoAuthPage
    body.text() == "noauth:${email}"
  }
}
