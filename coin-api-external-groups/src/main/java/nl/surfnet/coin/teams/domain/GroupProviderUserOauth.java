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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * User's key and secret for a Group provider
 */
public class GroupProviderUserOauth {
  private static final int PRIME = 31;
  private String personId;
  private String provider;
  private String oAuthToken;
  private String oAuthSecret;

  public GroupProviderUserOauth(String personId, String provider, String oAuthToken, String oAuthSecret) {
    this.personId = personId;
    this.oAuthSecret = oAuthSecret;
    this.oAuthToken = oAuthToken;
    this.provider = provider;
  }

  public String getPersonId() {
    return personId;
  }

  public String getProvider() {
    return provider;
  }

  public String getOAuthToken() {
    return oAuthToken;
  }

  public String getOAuthSecret() {
    return oAuthSecret;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    GroupProviderUserOauth oauth = (GroupProviderUserOauth) o;

    if (oAuthSecret != null ? !oAuthSecret.equals(oauth.oAuthSecret) : oauth.oAuthSecret != null) {
      return false;
    }
    if (oAuthToken != null ? !oAuthToken.equals(oauth.oAuthToken) : oauth.oAuthToken != null) {
      return false;
    }
    if (personId != null ? !personId.equals(oauth.personId) : oauth.personId != null) {
      return false;
    }
    if (provider != null ? !provider.equals(oauth.provider) : oauth.provider != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = personId != null ? personId.hashCode() : 0;
    result = PRIME * result + (provider != null ? provider.hashCode() : 0);
    result = PRIME * result + (oAuthToken != null ? oAuthToken.hashCode() : 0);
    result = PRIME * result + (oAuthSecret != null ? oAuthSecret.hashCode() : 0);
    return result;
  }

  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("personId", personId)
      .append("provider", provider)
      .append("oAuthToken", oAuthToken)
      .append("oAuthSecret", "*****")
      .toString();
  }
}
