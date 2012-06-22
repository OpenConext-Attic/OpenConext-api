/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.coin.api.client;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;

public class OpenConextApi20AuthorizationCode extends DefaultApi20 {

  private String baseUrl = "http://localhost:8095/";

  public OpenConextApi20AuthorizationCode() {
  }

  public OpenConextApi20AuthorizationCode(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Override
  public String getAccessTokenEndpoint() {
    return baseUrl + "oauth2/token?grant_type=authorization_code";
  }

  @Override
  public String getAuthorizationUrl(OAuthConfig config) {

    StringBuilder url = new StringBuilder(String.format(baseUrl + "oauth2/authorize?response_type=code&client_id=%s", config.getApiKey()));
    if (config.hasScope()) {
      url.append("&scope=").append(OAuthEncoder.encode(config.getScope()));
    }
    url.append("&redirect_uri=").append(OAuthEncoder.encode(config.getCallback()));
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
