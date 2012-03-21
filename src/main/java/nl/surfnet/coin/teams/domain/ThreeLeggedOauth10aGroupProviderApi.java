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

package nl.surfnet.coin.teams.domain;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.springframework.util.StringUtils;

import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.ACCESS_TOKEN_URL;
import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.AUTHORIZE_URL;
import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.REQUEST_METHOD;
import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.REQUEST_TOKEN_URL;

/**
 * {@link Api} implementation for an external group provider with 3 legged OAuth 1.0
 */
public class ThreeLeggedOauth10aGroupProviderApi extends DefaultApi10a {

  private final GroupProvider groupProvider;

  public ThreeLeggedOauth10aGroupProviderApi(GroupProvider groupProvider) {
    this.groupProvider = groupProvider;
  }

  /**
   * {@inheritDoc}
   *
   * @return if {@literal get} is configured {@link Verb#GET} otherwise {@link Verb#POST}
   */
  @Override
  public Verb getAccessTokenVerb() {
    String method = groupProvider.getAllowedOptionAsString(REQUEST_METHOD);
    if (Verb.GET.toString().equalsIgnoreCase(method)) {
      return Verb.GET;
    }
    return Verb.POST;
  }

  @Override
  public Verb getRequestTokenVerb() {
    return getAccessTokenVerb();
  }

  @Override
  public String getRequestTokenEndpoint() {
    return groupProvider.getAllowedOptionAsString(REQUEST_TOKEN_URL);
  }

  @Override
  public String getAccessTokenEndpoint() {
    return groupProvider.getAllowedOptionAsString(ACCESS_TOKEN_URL);
  }

  @Override
  public String getAuthorizationUrl(Token requestToken) {
    String authBaseUrl = (String) groupProvider.getAllowedOptions().get(AUTHORIZE_URL);
    if (StringUtils.hasText(authBaseUrl)) {
      return authBaseUrl + "?oauth_token=" + requestToken.getToken();
    }
    throw new IllegalArgumentException("Missing or incorrect Group Provider option " + AUTHORIZE_URL);
  }
}
