/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nl.surfnet.coin.api.oauth;

import javax.sql.DataSource;

import nl.surfnet.coin.api.shib.ShibbolethAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

/**
 * {@link TokenEnhancer} that stores the {@link ClientMetaData} within the Authentication object
 *
 */
@Component(value = "clientMetaDataTokenEnhancer")
public class ClientMetaDataTokenEnhancer implements TokenEnhancer {

  /* (non-Javadoc)
   * @see org.springframework.security.oauth2.provider.token.TokenEnhancer#enhance(org.springframework.security.oauth2.common.OAuth2AccessToken, org.springframework.security.oauth2.provider.OAuth2Authentication)
   */
  @Override
  public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
    Authentication userAuthentication = authentication.getUserAuthentication();
    if (userAuthentication instanceof ShibbolethAuthenticationToken) {
      ShibbolethAuthenticationToken shib = (ShibbolethAuthenticationToken) userAuthentication;
      ClientMetaDataHolder.storeClientMetaData(shib);
    } else {
      throw new RuntimeException("The userAuthentication is of the type '"
          + (userAuthentication != null ? userAuthentication.getClass() : "null")
          + "'. Required is a (sub)class of ShibbolethAuthenticationToken");
    }
    return accessToken;
  }

}
