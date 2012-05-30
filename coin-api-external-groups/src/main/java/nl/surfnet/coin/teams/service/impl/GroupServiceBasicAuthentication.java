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
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import nl.surfnet.coin.api.client.OpenConextJsonParser;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderUserOauth;
import nl.surfnet.coin.teams.domain.ThreeLeggedOauth10aGroupProviderApi;
import nl.surfnet.coin.teams.service.BasicAuthGroupService;
import nl.surfnet.coin.teams.service.OauthGroupService;
import nl.surfnet.coin.teams.util.GroupProviderOptionParameters;

import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.PROPERTY_DESCRIPTION;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.PROPERTY_NAME;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertProperty;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertToExternalGroupId;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertToExternalPersonId;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertToSurfConextGroupId;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertToSurfConextPersonId;

/**
 * Implementation for a {@link OauthGroupService} using 3-legged OAuth
 */
@Component(value = "basicAuthGroupService")
public class GroupServiceBasicAuthentication extends AbstractGroupService implements BasicAuthGroupService {

  @Override
  public Group20Entry getGroup20Entry(GroupProvider groupProvider, String personId, int limit, int offset) {

    String strippedPersonID = convertToExternalPersonId(personId, groupProvider);

    Client client = getClient(groupProvider);
    String url = MessageFormat.format("{0}/groups/{1}?startIndex={2}&count={3}",
        groupProvider.getAllowedOptionAsString(GroupProviderOptionParameters.URL), strippedPersonID, offset, limit);
    WebResource webResource = client.resource(url);

    InputStream response = webResource.get(InputStream.class);
    final Group20Entry entry = getGroup20EntryFromResponse(response, groupProvider);
    return entry;
  }

  @Override
  public Group20 getGroup20(GroupProvider groupProvider, String personId, String groupId) {
    String strippedPersonID = convertToExternalPersonId(personId, groupProvider);
    String strippedGroupID = convertToExternalGroupId(groupId, groupProvider);

    Client client = getClient(groupProvider);
    String url = MessageFormat.format("{0}/groups/{1}/{2}",
        groupProvider.getAllowedOptionAsString(GroupProviderOptionParameters.URL), strippedPersonID, strippedGroupID);
    WebResource webResource = client.resource(url);

    InputStream response = webResource.get(InputStream.class);
    final Group20Entry entry = getGroup20EntryFromResponse(response, groupProvider);
    if (entry == null) {
      return null;
    }
    final List<Group20> group20s = entry.getEntry();
    if (group20s != null && group20s.size() == 1) {
      return group20s.get(0);
    }
    throw new RuntimeException(String.format("Received %s groups for groupid %s", group20s.size(), groupId));

  }

  @Override
  public GroupMembersEntry getGroupMembersEntry(GroupProvider groupProvider,String personId, String groupId,
      int limit, int offset) {
    
    String strippedPersonID = convertToExternalPersonId(personId, groupProvider);
    String strippedGroupId = convertToExternalGroupId(groupId, groupProvider);

    Client client = getClient(groupProvider);
    String url = MessageFormat.format("{0}/people/{1}/{2}?startIndex={3}&count={4}&sortBy=familyName",
        groupProvider.getAllowedOptionAsString(GroupProviderOptionParameters.URL), strippedPersonID,strippedGroupId, offset, limit);
    WebResource webResource = client.resource(url);

    String response = webResource.get(String.class);
    InputStream in = new ByteArrayInputStream(response.getBytes());

    GroupMembersEntry entry = getGroupMembersEntryFromResponse(in, groupProvider);
    return entry;
  }

  private Client getClient(GroupProvider groupProvider) {
    String user = groupProvider.getAllowedOptionAsString("user");
    String password = groupProvider.getAllowedOptionAsString("password");

    Client client = Client.create();
    client.addFilter(new HTTPBasicAuthFilter(user, password));
    return client;
  }



}
