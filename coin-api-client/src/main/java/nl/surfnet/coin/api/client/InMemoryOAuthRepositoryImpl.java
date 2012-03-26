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

package nl.surfnet.coin.api.client;

import java.util.HashMap;
import java.util.Map;

import org.scribe.model.Token;

/**
 * InMemory Repository for Tokens
 * 
 */
public class InMemoryOAuthRepositoryImpl implements OAuthRepository {

  private Map<String, OAuthToken> tokens = new HashMap<String, OAuthToken>();

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.api.client.OAuthRepository#getToken(java.lang.String)
   */
  @Override
  public OAuthToken getToken(String userId) {
    return tokens.get(userId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OAuthRepository#storeToken(org.scribe.model.
   * Token, java.lang.String)
   */
  @Override
  public void storeToken(Token accessToken, String userId, OAuthVersion version) {
    tokens.put(userId, new OAuthToken(accessToken, version));
  }

}
