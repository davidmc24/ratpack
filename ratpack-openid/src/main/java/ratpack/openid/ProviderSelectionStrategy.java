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
import ratpack.handling.Handler;

// TODO: document
// TODO: provide an example of multiple or user-controlled providers
public interface ProviderSelectionStrategy {
  /**
   * Returns the handler to use for provider selection, or {@code null} if no handler is needed.
   */
  Handler getHandler();

  /**
   * Executes whatever logic is needed to determine which provider to use.  This may include redirecting the user to
   * a selection screen, in which case this method should return {@code false}.
   *
   * @param context the context to handle
   * @return whether a provider has been fully selected and request handling should continue
   */
  boolean handleProviderSelection(Context context);

  /**
   * Returns the OpenID Provider Discovery URL to use for authentication.
   */
  String getProviderDiscoveryUrl();
}
