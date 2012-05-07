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

import javax.annotation.Resource;

import nl.surfnet.coin.api.shib.ShibbolethAuthenticationToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

/**
 * {@link TokenEnhancer} that stores the {@link ClientMetaData} within the
 * Authentication object
 * 
 */
@Component(value = "clientMetaDataTokenEnhancer")
public class ClientMetaDataTokenEnhancer implements TokenEnhancer {

  @Resource(name = "janusClientDetailsService")
  private ClientDetailsService clientDetailsService;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.security.oauth2.provider.token.TokenEnhancer#enhance
   * (org.springframework.security.oauth2.common.OAuth2AccessToken,
   * org.springframework.security.oauth2.provider.OAuth2Authentication)
   */
  @Override
  public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
    AuthorizationRequest authorizationRequest = authentication.getAuthorizationRequest();
    String clientId = authorizationRequest.getClientId();
    ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
    if (clientDetails instanceof ExtendedBaseClientDetails) {
      ClientMetaData clientMetaData = ((ExtendedBaseClientDetails) clientDetails).getClientMetaData();
      Authentication userAuthentication = authentication.getUserAuthentication();
      if (userAuthentication instanceof ShibbolethAuthenticationToken) {
        ((ShibbolethAuthenticationToken) userAuthentication).setClientMetaData(clientMetaData);
      } else {
        throw new RuntimeException("The userAuthentication is of the type '"
            + (userAuthentication != null ? userAuthentication.getClass() : "null")
            + "'. Required is a (sub)class of ShibbolethAuthenticationToken");
      }
    }
    /*
     * Part of the method contract. We did however change the
     * OAuth2Authentication and that is stored as a blob in the database, so the
     * metadata is accessible later on when checking for ACL's against the SP
     */
    return accessToken;
  }

}
