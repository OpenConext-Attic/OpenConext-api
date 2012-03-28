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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.surfnet.coin.api.client.OpenConextJsonParser;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderUserOauth;
import nl.surfnet.coin.teams.domain.ThreeLeggedOauth10aGroupProviderApi;
import nl.surfnet.coin.teams.service.GroupProviderService;
import nl.surfnet.coin.teams.service.GroupService;
import nl.surfnet.coin.teams.util.GroupProviderOptionParameters;

import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.PROPERTY_DESCRIPTION;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.PROPERTY_NAME;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertProperty;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertToExternalPersonId;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertToSurfConextGroupId;

/**
 * Implementation for a {@link GroupService} using 3-legged OAuth
 */
@Service
public class GroupServiceThreeLeggedOAuth10a implements GroupService {
  private static Logger log = LoggerFactory.getLogger(GroupServiceThreeLeggedOAuth10a.class);

  private ObjectMapper objectMapper = new ObjectMapper();

  private OpenConextJsonParser parser = new OpenConextJsonParser();

  @Override
  public List<Group20> getGroup20s(GroupProviderUserOauth oauth, GroupProvider provider) {

    // we assume now that it's a 3-legged oauth provider
    final ThreeLeggedOauth10aGroupProviderApi api =
        new ThreeLeggedOauth10aGroupProviderApi(provider);
    final GroupProviderServiceThreeLeggedOAuth10a tls =
        new GroupProviderServiceThreeLeggedOAuth10a(provider, api);
    final OAuthService oAuthService = tls.getOAuthService();

    Token accessToken = new Token(oauth.getOAuthToken(), oauth.getOAuthSecret());
    final String strippedID = convertToExternalPersonId(oauth.getPersonId(), provider);

    OAuthRequest oAuthRequest = new OAuthRequest(api.getRequestTokenVerb(),
        MessageFormat.format("{0}/groups/{1}",
            provider.getAllowedOptionAsString(GroupProviderOptionParameters.URL),
            strippedID));
    oAuthService.signRequest(accessToken, oAuthRequest);
    Response oAuthResponse = oAuthRequest.send();

    if (oAuthResponse.isSuccessful()) {
      return getGroup20sFromResponse(oAuthResponse, provider);
    } else {
      log.info("Fetching external groups for user {} failed with status code {}",
          oauth.getPersonId(), oAuthResponse.getCode());
      log.trace(oAuthResponse.getBody());
    }
    return new ArrayList<Group20>();
  }

  private List<Group20> getGroup20sFromResponse(Response oAuthResponse, GroupProvider groupProvider) {
    List<Group20> groups = new ArrayList<Group20>();
    String body = oAuthResponse.getBody();
    InputStream in = new ByteArrayInputStream(body.getBytes());

    try {
      final JsonNode jsonNodes = objectMapper.readTree(body);

      if (jsonNodes.has("result")) {
        groups = parser.parseGroup20ResultWrapper(in).getResult().getEntry();
      } else if (jsonNodes.has("entry")) {
        groups = parser.parseGroups20(in).getEntry();
      }
    } catch (IOException e) {
      throw new RuntimeException("Could not parse response into JSON objects", e);
    }

    for (Group20 group20: groups) {
      String scGroupId = convertToSurfConextGroupId(group20.getId(),
          groupProvider);
      group20.setId(scGroupId);
      
      String scGroupName = convertProperty(PROPERTY_NAME,
          group20.getTitle(), groupProvider.getGroupFilters());
      group20.setTitle(scGroupName);
      
      String scGroupDesc = convertProperty(PROPERTY_DESCRIPTION,
          group20.getDescription(), groupProvider.getGroupFilters());

      group20.setDescription(scGroupDesc);
    }

    return groups;
  }

}
