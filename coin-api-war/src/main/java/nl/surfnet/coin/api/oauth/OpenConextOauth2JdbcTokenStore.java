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

import java.sql.Types;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.JdbcTokenStore;

import nl.surfnet.coin.api.saml.SAMLAuthenticationToken;

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

  private JdbcTemplate jdbcTemplate;

  /**
   * @param dataSource
   */
  public OpenConextOauth2JdbcTokenStore(DataSource dataSource) {
    super(dataSource);
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
    AuthorizationRequest authorizationRequest = authentication.getAuthorizationRequest();
    Authentication userAuthentication = authentication.getUserAuthentication();
    if (userAuthentication instanceof SAMLAuthenticationToken) {
      SAMLAuthenticationToken samlToken = (SAMLAuthenticationToken) userAuthentication;
      if (samlToken.getClientMetaData() == null) {
        String clientId = authorizationRequest.getClientId();
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails instanceof OpenConextClientDetails) {
          ClientMetaData clientMetaData = ((OpenConextClientDetails) clientDetails).getClientMetaData();
          samlToken.setClientMetaData(clientMetaData);
        } else {
          throw new RuntimeException("The clientDetails is of the type '"
              + (clientDetails != null ? clientDetails.getClass() : "null")
              + "'. Required is a (sub)class of ExtendedBaseClientDetails");
        }
        
      }

      String refreshToken = null;
      if (token.getRefreshToken() != null) {
        refreshToken = token.getRefreshToken().getValue();
      }


      jdbcTemplate.update(
          ACCESS_TOKEN_INSERT_STATEMENT,
          new Object[] { token.getValue(), new SqlLobValue(SerializationUtils.serialize(token)),
              authenticationKeyGenerator.extractKey(authentication),
              authentication.isClientOnly() ? null : authentication.getName(),
              authentication.getAuthorizationRequest().getClientId(),
              samlToken.getClientMetaData().getAppEntityId(),
              new SqlLobValue(SerializationUtils.serialize(authentication)), refreshToken }, new int[] {
          Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB,
          Types.VARCHAR });


    } else {
      throw new RuntimeException("The userAuthentication is of the type '"
          + (userAuthentication != null ? userAuthentication.getClass() : "null")
          + "'. Required is a (sub)class of SAMLAuthenticationToken");
    }

  }

  private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

  public static final String ACCESS_TOKEN_INSERT_STATEMENT = "insert into oauth_access_token " +
      "(token_id, token, authentication_id, user_name, client_id, client_entity_id, authentication, refresh_token) " +
      "values (?, ?, ?, ?, ?, ?, ?, ?)";

}
