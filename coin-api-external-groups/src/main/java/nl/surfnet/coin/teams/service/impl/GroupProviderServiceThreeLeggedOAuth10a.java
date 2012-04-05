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

package nl.surfnet.coin.teams.service.impl;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.SignatureType;
import org.scribe.oauth.OAuthService;

import nl.surfnet.coin.teams.domain.GroupProvider;

import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.CONSUMER_KEY;
import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.CONSUMER_SECRET;
import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.REQUEST_SCHEME;

/**
 * Util class to return an {@link OAuthService} for 3-legged OAuth 1.0a group providers
 */
public class GroupProviderServiceThreeLeggedOAuth10a {

  private final GroupProvider groupProvider;
  private final Api oAuthApi;

  /**
   * Constructor for this service
   *
   * @param groupProvider the configured {@link GroupProvider}
   * @param oAuthApi      an {@link Api} implementation,
   *                      probably {@link nl.surfnet.coin.teams.domain.ThreeLeggedOauth10aGroupProviderApi}
   */
  public GroupProviderServiceThreeLeggedOAuth10a(GroupProvider groupProvider,
                                                 Api oAuthApi) {
    this.groupProvider = groupProvider;
    this.oAuthApi = oAuthApi;
  }

  /**
   * Util method to return an {@link OAuthService} for this kind of group provider
   *
   * @return {@link OAuthService}
   */
  public final OAuthService getOAuthService() {
    String s = groupProvider.getAllowedOptionAsString(REQUEST_SCHEME);
    SignatureType st;
    if (SignatureType.QueryString.toString().equalsIgnoreCase(s)) {
      st = SignatureType.QueryString;
    } else {
      st = SignatureType.Header;
    }

    OAuthService service = new ServiceBuilder()
        .provider(oAuthApi)
        .apiKey(groupProvider.getAllowedOptionAsString(CONSUMER_KEY))
        .apiSecret(groupProvider.getAllowedOptionAsString(CONSUMER_SECRET))
        .signatureType(st)
        .build();
    return service;
  }

}
