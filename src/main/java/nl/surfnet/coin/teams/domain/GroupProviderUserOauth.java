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

package nl.surfnet.coin.teams.domain;

/**
User's key and secret for a Group provider
 */
public class GroupProviderUserOauth {
  private String provider;
  private String oAuthToken;
  private String oAuthSecret;

  public GroupProviderUserOauth(String provider, String oAuthToken, String oAuthSecret) {

    this.oAuthSecret = oAuthSecret;
    this.oAuthToken = oAuthToken;
    this.provider = provider;
  }

  public String getProvider() {
    return provider;
  }

  public String getoAuthToken() {
    return oAuthToken;
  }

  public String getoAuthSecret() {
    return oAuthSecret;
  }
}
