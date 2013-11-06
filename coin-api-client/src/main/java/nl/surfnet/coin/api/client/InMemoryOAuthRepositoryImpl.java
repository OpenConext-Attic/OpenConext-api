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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InMemory Repository for Tokens.
 *
 * Uses the special 'null' userId for storing the token that is not user-specific (like for client credentials)
 * 
 */
public class InMemoryOAuthRepositoryImpl implements OAuthRepository {

  private Map<String, String> userSpecificTokens = new ConcurrentHashMap<String, String>();

  private String nonUserSpecificToken = null;

  @Override
  public String getToken(String userId) {
    return userId == null ? nonUserSpecificToken : userSpecificTokens.get(userId);
  }

  @Override
  public void storeToken(String accessToken, String userId) {
    if (userId == null) {
      nonUserSpecificToken = accessToken;
    } else {
      userSpecificTokens.put(userId, accessToken);
    }
  }

  @Override
  public void removeToken(String onBehalfOf) {
    if (onBehalfOf == null) {
      nonUserSpecificToken = null;
    } else {
      userSpecificTokens.remove(onBehalfOf);
    }
  }
}
