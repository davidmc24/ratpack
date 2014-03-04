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
import ratpack.session.store.SessionStorage;

import static ratpack.openid.SessionConstants.USER;

// TODO: document
public abstract class SecurityUtils {
  public static OpenIdUser getUser(Context context) {
    return (OpenIdUser) context.get(SessionStorage.class).get(USER);
  }

  public static boolean isAuthenticated(Context context) {
    return getUser(context) != null;
  }
}
