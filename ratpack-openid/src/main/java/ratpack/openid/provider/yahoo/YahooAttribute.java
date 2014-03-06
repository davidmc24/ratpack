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

package ratpack.openid.provider.yahoo;

import org.openid4java.message.MessageException;
import org.openid4java.message.ax.FetchRequest;
import ratpack.openid.Attribute;

/**
 * Attributes supported by Yahoo's OpenID provider.
 */
public enum YahooAttribute implements Attribute {
  email("email", "http://axschema.org/contact/email"),
  fullname("fullname", "http://axschema.org/namePerson");

  private final String alias;
  private final String typeUri;

  YahooAttribute(String alias, String typeUri) {
    this.alias = alias;
    this.typeUri = typeUri;
  }

  @Override
  public void register(FetchRequest fetchRequest, boolean required) throws MessageException {
    fetchRequest.addAttribute(alias, typeUri, required, 1);
  }
}
