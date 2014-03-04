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

import ratpack.handling.Context;

import java.util.regex.Pattern;

// TODO: document
public class AuthenticationRequirement {
  private final Pattern pattern;

  private AuthenticationRequirement(Pattern pattern) {
    this.pattern = pattern;
  }

  boolean matches(Context context) {
    String uri = context.getRequest().getUri();
    return pattern.matcher(uri).matches();
  }

  public static AuthenticationRequirement of(String path) {
    return new AuthenticationRequirement(Pattern.compile(Pattern.quote(path)));
  }

  public static AuthenticationRequirement of(Pattern pattern) {
    return new AuthenticationRequirement(pattern);
  }
}
