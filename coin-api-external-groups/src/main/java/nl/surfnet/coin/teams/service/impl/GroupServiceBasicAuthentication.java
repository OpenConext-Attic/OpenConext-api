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
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.service.BasicAuthGroupService;
import nl.surfnet.coin.teams.util.GroupProviderOptionParameters;

import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertToExternalGroupId;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertToExternalPersonId;

/**
 * Implementation for {@link BasicAuthGroupService} to supply external group
 * providers based on basic auth
 */
@Component(value = "basicAuthGroupService")
public class GroupServiceBasicAuthentication extends AbstractGroupService implements BasicAuthGroupService {

  private Client client = Client.create();
  
  @Override
  public Group20Entry getGroup20Entry(GroupProvider groupProvider, String personId, int limit, int offset) {

    String strippedPersonID = convertToExternalPersonId(personId, groupProvider);

    Client client = getClient(groupProvider);
    String url = String.format("%s/groups/%s?startIndex=%s&count=%s",
        groupProvider.getAllowedOptionAsString(GroupProviderOptionParameters.URL), strippedPersonID, offset, limit);
    WebResource webResource = client.resource(url);
    Group20Entry entry = getGroup20Entry(groupProvider, webResource);
    return entry;
  }

  @Override
  public Group20 getGroup20(GroupProvider groupProvider, String personId, String groupId) {
    String strippedPersonID = convertToExternalPersonId(personId, groupProvider);
    String strippedGroupID = convertToExternalGroupId(groupId, groupProvider);

    Client client = getClient(groupProvider);
    String url = String.format("%s/groups/%s/%s",
        groupProvider.getAllowedOptionAsString(GroupProviderOptionParameters.URL), strippedPersonID, strippedGroupID);
    WebResource webResource = client.resource(url);
    Group20Entry entry = getGroup20Entry(groupProvider, webResource);
    if (entry == null || entry.getEntry() == null || entry.getEntry().isEmpty()) {
      return null;
    }
    final List<Group20> group20s = entry.getEntry();
    if (group20s != null && group20s.size() == 1) {
      return group20s.get(0);
    }
    throw new RuntimeException(String.format("Received %s groups for groupid %s", group20s.size(), groupId));

  }

  private Group20Entry getGroup20Entry(GroupProvider groupProvider,
      WebResource webResource) {
    Group20Entry entry = null;
    try {
      InputStream response = webResource.get(InputStream.class);
      entry = getGroup20EntryFromResponse(response, groupProvider);
    } catch (UniformInterfaceException e) {
      log.error("Received error from "+groupProvider,e);
      //intentional as we don't want the flow of other groupProviders to end
    }
    return entry;
  }

  @Override
  public GroupMembersEntry getGroupMembersEntry(GroupProvider groupProvider, String personId, String groupId,
      int limit, int offset) {

    String strippedPersonID = convertToExternalPersonId(personId, groupProvider);
    String strippedGroupId = convertToExternalGroupId(groupId, groupProvider);

    Client client = getClient(groupProvider);
    String url = String.format("%s/people/%s/%s?startIndex=%s&count=%s&sortBy=name.familyName",
        groupProvider.getAllowedOptionAsString(GroupProviderOptionParameters.URL), strippedPersonID, strippedGroupId,
        offset, limit);
    WebResource webResource = client.resource(url);
    String response;
    try {
      response = webResource.get(String.class);
    } catch (UniformInterfaceException e) {
      log.error("Received error from "+groupProvider,e);
      //intentional as we don't want the flow of other groupProviders to end
      return new GroupMembersEntry(new ArrayList<Person>());
    }     
    InputStream in = new ByteArrayInputStream(response.getBytes());

    GroupMembersEntry entry = getGroupMembersEntryFromResponse(in, groupProvider);
    return entry;
  }

  private Client getClient(GroupProvider groupProvider) {
    String user = groupProvider.getAllowedOptionAsString(GroupProviderOptionParameters.USERNAME);
    String password = groupProvider.getAllowedOptionAsString(GroupProviderOptionParameters.PASSWORD);
    client.removeAllFilters();
    client.addFilter(new HTTPBasicAuthFilter(user, password));
    return client;
  }

}
