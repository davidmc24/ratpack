/*
 * Copyright 2013 the original author or authors.
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

package ratpack.parse;

import ratpack.util.internal.Types;

/**
 * Convenience base for {@link Parse} implementations.
 * <p>
 * If implementing a parse type with no “options” beyond the {@link #getType()} method, consider using {@link NoOptParse} instead.
 * <p>
 * If implementing a parse type that does provide extra options to the parser implementation, this class can be used as the base.
 * <p>
 * See {@link ParserSupport} for usage examples.
 *
 * @param <T> The type of object to parse to
 */
public class ParseSupport<T> implements Parse<T> {

  private final Class<T> type;

  /**
   * Constructor.
   */
  protected ParseSupport() {
    this.type = Types.findImplParameterTypeAtIndex(getClass(), ParseSupport.class, 0);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<T> getType() {
    return type;
  }

}
