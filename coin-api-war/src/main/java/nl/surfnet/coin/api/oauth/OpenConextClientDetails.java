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

import org.springframework.security.oauth2.provider.BaseClientDetails;

/**
 * {@link BaseClientDetails} with extra info for OpenConext
 *
 */
@SuppressWarnings("serial")
public class OpenConextClientDetails extends BaseClientDetails {

  private ClientMetaData clientMetaData;

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

  /* (non-Javadoc)
   * @see org.springframework.security.oauth2.provider.BaseClientDetails#getAccessTokenValiditySeconds()
   */
  @Override
  public int getAccessTokenValiditySeconds() {
    return 0;
  }

}
