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
import javax.sql.DataSource;

import nl.surfnet.coin.api.shib.ShibbolethAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.JdbcTokenStore;

/**
 * {@link JdbcTokenStore} that add the ClientMetaData from Janus if it is not
 * present. The reason for this subclass is that the original
 * JdbcTokenStore#getAccessToken does also do a remove and a new insert if the
 * authentication has changed. We don't have the hook to enhance the
 * Authentication in this case (as we do with the normal
 * DefaultTokenServices#createAccessToken where we have a
 * ClientMetaDataTokenEnhancer that does the lookup of the details in Janus.
 * 
 */
public class OpenConextOauth2JdbcTokenStore extends JdbcTokenStore {

  @Resource(name = "janusClientDetailsService")
  private ClientDetailsService clientDetailsService;

  /**
   * @param dataSource
   */
  public OpenConextOauth2JdbcTokenStore(DataSource dataSource) {
    super(dataSource);

  }

  @Override
  public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
    AuthorizationRequest authorizationRequest = authentication.getAuthorizationRequest();
    Authentication userAuthentication = authentication.getUserAuthentication();
    if (userAuthentication instanceof ShibbolethAuthenticationToken) {
      ShibbolethAuthenticationToken shibAuth = (ShibbolethAuthenticationToken) userAuthentication;
      if (shibAuth.getClientMetaData() == null) {
        String clientId = authorizationRequest.getClientId();
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails instanceof OpenConextClientDetails) {
          ClientMetaData clientMetaData = ((OpenConextClientDetails) clientDetails).getClientMetaData();
          shibAuth.setClientMetaData(clientMetaData);
        } else {
          throw new RuntimeException("The clientDetails is of the type '"
              + (clientDetails != null ? clientDetails.getClass() : "null")
              + "'. Required is a (sub)class of ExtendedBaseClientDetails");
        }
        
      }    
    } else {
      throw new RuntimeException("The userAuthentication is of the type '"
          + (userAuthentication != null ? userAuthentication.getClass() : "null")
          + "'. Required is a (sub)class of ShibbolethAuthenticationToken");
    }    
    super.storeAccessToken(token, authentication);
  }

}
