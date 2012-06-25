/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.coin.api.client.internal;

import org.scribe.model.Token;

import nl.surfnet.coin.api.client.OAuthVersion;

/**
 * 
 *
 */
public class OAuthToken {
  private Token token;
  private OAuthVersion version;

  public OAuthToken(Token token, OAuthVersion version) {
    super();
    this.token = token;
    this.version = version;
  }

  /**
   * @return the token
   */
  public Token getToken() {
    return token;
  }

  /**
   * @return the version
   */
  public OAuthVersion getVersion() {
    return version;
  }
  
  
}
