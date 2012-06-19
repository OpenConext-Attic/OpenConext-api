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

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.TokenServicesUserApprovalHandler;

/**
 * {@link TokenServicesUserApprovalHandler} that can skip consent based on the
 * metadata of the sp
 * 
 */
public class ConfigurableTokenServicesUserApprovalHandler extends TokenServicesUserApprovalHandler {

  private ClientDetailsService clientDetailsService;

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.security.oauth2.provider.approval.
   * TokenServicesUserApprovalHandler
   * #isApproved(org.springframework.security.oauth2
   * .provider.AuthorizationRequest,
   * org.springframework.security.core.Authentication)
   */
  @Override
  public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
    String clientId = authorizationRequest.getClientId();
    ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
    if (clientDetails instanceof OpenConextClientDetails) {
      ClientMetaData clientMetaData = ((OpenConextClientDetails) clientDetails).getClientMetaData();
      if (!clientMetaData.isConsentRequired()) {
        return userAuthentication.isAuthenticated(); 
      }
    }
    return super.isApproved(authorizationRequest, userAuthentication);
  }

  /**
   * @param clientDetailsService
   *          the clientDetailsService to set
   */
  public void setClientDetailsService(ClientDetailsService clientDetailsService) {
    this.clientDetailsService = clientDetailsService;
  }

}
