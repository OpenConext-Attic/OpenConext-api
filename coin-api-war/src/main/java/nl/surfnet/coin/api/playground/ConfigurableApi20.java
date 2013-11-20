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
package nl.surfnet.coin.api.playground;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * @author oharsta
 * 
 */
public class ConfigurableApi20 extends DefaultApi20 {

  private String accessTokenEndPoint;
  private String authorizationUrl;
  private String grantType;

  public ConfigurableApi20(String accessTokenEndPoint, String authorizationUrl, String grantType) {
    super();
    this.accessTokenEndPoint = accessTokenEndPoint;
    this.authorizationUrl = authorizationUrl;
    this.grantType = grantType;
  }

  @Override
  public OAuthService createService(OAuthConfig config) {
    return new ConfigurableOAuth20ServiceImpl(this, config);
  }

  @Override
  public String getAccessTokenEndpoint() {
    switch (grantType) {
      case "authCode":
        return accessTokenEndPoint + "?grant_type=authorization_code";
      case "clientCredentials":
        return  accessTokenEndPoint + "?grant_type=client_credentials";
      default:
        throw new IllegalStateException("unknown grant type: " + grantType);
    }
  }

  @Override
  public String getAuthorizationUrl(OAuthConfig config) {
    String type = (grantType.equalsIgnoreCase("authCode") ? "code" : "token");
    StringBuilder url = new StringBuilder(String.format(authorizationUrl + "?response_type=%s&client_id=%s", type,
        config.getApiKey()));
    if (config.hasScope()) {
      url.append("&scope=").append(config.getScope());
    }
    if (config.getCallback() != null) {
      url.append("&redirect_uri=").append(config.getCallback());
    }
    return url.toString();
  }

  public Verb getAccessTokenVerb() {
    return Verb.POST;
  }

  @Override
  public AccessTokenExtractor getAccessTokenExtractor() {
    // OpenConext (Spring Security OAuth2) sends JSON.
    return new JsonTokenExtractor();
  }

}
