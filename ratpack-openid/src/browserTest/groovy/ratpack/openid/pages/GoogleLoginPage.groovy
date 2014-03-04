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

package ratpack.openid.pages

import geb.Page

class GoogleLoginPage extends Page {
  static at = {
    driver.currentUrl.startsWith("https://accounts.google.com/ServiceLogin")
  }

  static content = {
    emailField { $("input", id: "Email") }
    passwordField { $("input", id: "Passwd") }
    persistentCookieCheckbox { $("input", id: "PersistentCookie") }
    signInButton { $("input", id: "signIn") }
  }

  void login(String email, String password, boolean stayLoggedIn = false) {
    emailField.value(email)
    passwordField.value(password)
    persistentCookieCheckbox.value(stayLoggedIn)
    signInButton.click(GoogleAuthorizationPage)
  }
}
