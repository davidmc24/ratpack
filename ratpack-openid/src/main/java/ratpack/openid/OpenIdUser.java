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

import ratpack.util.MultiValueMap;
import ratpack.util.internal.ImmutableDelegatingMultiValueMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

// TODO: document
public class OpenIdUser implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String identifier;
  private final Map<String, List<String>> attributes;

  OpenIdUser(String identifier, Map<String, List<String>> attributes) {
    this.identifier = identifier;
    this.attributes = attributes;
  }

  public String getIdentifier() {
    return identifier;
  }

  public MultiValueMap<String, String> getAttributes() {
    return new ImmutableDelegatingMultiValueMap<>(attributes);
  }
}
