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

import java.io.Serializable;
import java.security.Principal;

/**
 * Principal returned by SAMLAssertionAuthenticationFilter that is stored as
 * a blob in the oauth1_tokens table for access (when we need to enforce SP
 * GroupProvider ACL's)
 * 
 */
@SuppressWarnings("serial")
public class ClientMetaDataPrincipal implements Serializable, Principal {

  private String remoteUser;
  private ClientMetaData clientMetaData;
  
  /**
   * @param remoteUser
   */
  public ClientMetaDataPrincipal(String remoteUser) {
    this.remoteUser = remoteUser;
  }

  /**
   * @return the clientMetaData
   */
  public ClientMetaData getClientMetaData() {
    return clientMetaData;
  }

  /**
   * @param clientMetaData the clientMetaData to set
   */
  public void setClientMetaData(ClientMetaData clientMetaData) {
    this.clientMetaData = clientMetaData;
  }

  /**
   * @return the remoteUser
   */
  public String getRemoteUser() {
    return remoteUser;
  }

  /* (non-Javadoc)
   * @see java.security.Principal#getName()
   */
  @Override
  public String getName() {
    return getRemoteUser();
  }

}
