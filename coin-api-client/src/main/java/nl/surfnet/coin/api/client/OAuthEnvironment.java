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
package nl.surfnet.coin.api.client;

/**
 * Environment depended variables for connecting to an OAuth OpenSocial endpoint
 * 
 */
public class OAuthEnvironment {

  private String oauthKey;
  private String oauthSecret;
  private String endpointBaseUrl;
  private String callbackUrl;

  /**
   * @return the oauthKey
   */
  public String getOauthKey() {
    return oauthKey;
  }

  /**
   * @param oauthKey
   *          the oauthKey to set
   */
  public void setOauthKey(String oauthKey) {
    this.oauthKey = oauthKey;
  }

  /**
   * @return the oauthSecret
   */
  public String getOauthSecret() {
    return oauthSecret;
  }

  /**
   * @param oauthSecret
   *          the oauthSecret to set
   */
  public void setOauthSecret(String oauthSecret) {
    this.oauthSecret = oauthSecret;
  }

  /**
   * @return the endpointBaseUrl
   */
  public String getEndpointBaseUrl() {
    return endpointBaseUrl;
  }

  /**
   * @param endpointBaseUrl
   *          the endpointBaseUrl to set
   */
  public void setEndpointBaseUrl(String endpointBaseUrl) {
    this.endpointBaseUrl = endpointBaseUrl;
  }

  /**
   * @return the callbackUrl
   */
  public String getCallbackUrl() {
    return callbackUrl;
  }

  /**
   * @param callbackUrl the callbackUrl to set
   */
  public void setCallbackUrl(String callbackUrl) {
    this.callbackUrl = callbackUrl;
  }

}
