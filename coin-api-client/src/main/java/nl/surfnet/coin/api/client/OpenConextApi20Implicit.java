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
import org.scribe.model.OAuthConfig;

public class OpenConextApi20Implicit extends DefaultApi20 {

  private String baseUrl = "http://localhost:8095/";


  public OpenConextApi20Implicit() {
  }

  public OpenConextApi20Implicit(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  /**
   * Throws an IllegalStateException as this method call is not applicable with OAuth 2.0 implicit grant.
   * @return
   */
  @Override
  public String getAccessTokenEndpoint() {
    throw new  IllegalStateException("N/A with implicit grant.");
  }

  @Override
  public String getAuthorizationUrl(OAuthConfig config) {
    StringBuilder url = new StringBuilder(String.format(baseUrl + "oauth2/authorize?response_type=token&client_id=%s", config.getApiKey()));
    if (config.hasScope()) {
      url.append("&scope=").append(config.getScope());
    }
    if (config.getCallback() != null && !config.getCallback().equals("oob")) {
      url.append("&redirect_uri=").append(config.getCallback());
    }
    return url.toString();
  }
}
