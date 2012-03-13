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

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;

/**
 * Thrre legged Api.
 * 
 */
public class OpenConextApi20ThreeLeggedDeletme extends DefaultApi20 {

  private String baseUrl;

  public OpenConextApi20ThreeLeggedDeletme(String baseUrl) {
    super();
    this.baseUrl = withEndingSlash(baseUrl);
  }

  private String withEndingSlash(String path) {
    return path.endsWith("/") ? path : path + "/";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.scribe.builder.api.DefaultApi20#getAccessTokenEndpoint()
   */
  @Override
  public String getAccessTokenEndpoint() {
    return baseUrl + "oauth2/accessToken";
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.scribe.builder.api.DefaultApi20#getAuthorizationUrl(org.scribe.model
   * .OAuthConfig)
   */
  @Override
  public String getAuthorizationUrl(OAuthConfig config) {
    return String.format(baseUrl
        + "oauth2/authorize?client_id=%s&response_type=code&redirect_uri=%s",
        config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
  }

}
