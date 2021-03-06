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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderUserOauth;
import nl.surfnet.coin.teams.domain.ThreeLeggedOauth10aGroupProviderApi;
import nl.surfnet.coin.teams.service.OauthGroupService;
import nl.surfnet.coin.teams.util.GroupProviderOptionParameters;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertToExternalGroupId;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertToExternalPersonId;

/**
 * Implementation for a {@link OauthGroupService} using 3-legged OAuth
 */
@Component(value = "oauthGroupService")
public class GroupServiceThreeLeggedOAuth10a extends AbstractGroupService implements OauthGroupService {
  private static Logger log = LoggerFactory.getLogger(GroupServiceThreeLeggedOAuth10a.class);

  private static final int MAX_ITEMS = 1000;

  @Override
  public Group20Entry getGroup20Entry(GroupProviderUserOauth oauth, GroupProvider groupProvider, int limit, int offset) {

    // we assume now that it's a 3-legged oauth provider
    final ThreeLeggedOauth10aGroupProviderApi api = new ThreeLeggedOauth10aGroupProviderApi(groupProvider);
    final GroupProviderServiceThreeLeggedOAuth10a tls = new GroupProviderServiceThreeLeggedOAuth10a(groupProvider, api);
    final OAuthService oAuthService = tls.getOAuthService();

    Token accessToken = new Token(oauth.getOAuthToken(), oauth.getOAuthSecret());
    final String strippedID = convertToExternalPersonId(oauth.getPersonId(), groupProvider);

    OAuthRequest oAuthRequest = getGroupListOAuthRequest(groupProvider, api, strippedID);
    oAuthRequest.addQuerystringParameter("startIndex", Integer.toString(offset));
    oAuthRequest.addQuerystringParameter("count", Integer.toString(limit));
    oAuthService.signRequest(accessToken, oAuthRequest);

    log.info("About to get group information with group provider {} using URL: {}", groupProvider.getIdentifier(), oAuthRequest);

    Response oAuthResponse = oAuthRequest.send();
    if (oAuthResponse.isSuccessful()) {
      final Group20Entry entry = getGroup20EntryFromResponse(oAuthResponse.getStream(), groupProvider);
      log.debug("Got group entry: {}", entry);

      return entry;
    } else {
      log.warn("Fetching external groups for GroupProvider {} for user {} failed with status code {}",
        groupProvider, oauth.getPersonId(), oAuthResponse.getCode());
      log.warn(oAuthResponse.getBody());
    }
    return null;
  }

  @Override
  public List<Group20> getGroup20List(GroupProviderUserOauth oauth, GroupProvider provider) {
    final Group20Entry group20Entry = getGroup20Entry(oauth, provider, MAX_ITEMS, 0);
    return group20Entry == null ? new ArrayList<Group20>() : group20Entry.getEntry();
  }

  @Override
  public Group20 getGroup20(GroupProviderUserOauth oauth, GroupProvider groupProvider, String groupId) {
    // we assume now that it's a 3-legged oauth provider
    final ThreeLeggedOauth10aGroupProviderApi api = new ThreeLeggedOauth10aGroupProviderApi(groupProvider);
    final GroupProviderServiceThreeLeggedOAuth10a tls = new GroupProviderServiceThreeLeggedOAuth10a(groupProvider, api);
    final OAuthService oAuthService = tls.getOAuthService();

    Token accessToken = new Token(oauth.getOAuthToken(), oauth.getOAuthSecret());
    final String strippedPersonID = convertToExternalPersonId(oauth.getPersonId(), groupProvider);
    final String strippedGroupID = convertToExternalGroupId(groupId, groupProvider);
    final OAuthRequest oAuthRequest = getGroupOAuthRequest(groupProvider, api, strippedPersonID, strippedGroupID);
    oAuthService.signRequest(accessToken, oAuthRequest);

    log.info("About to get group information with group provider {} for group {} using URL: {}", groupProvider.getIdentifier(), groupId, oAuthRequest);

    Response oAuthResponse = oAuthRequest.send();
    if (oAuthResponse.isSuccessful()) {
      final Group20Entry group20Entry = getGroup20EntryFromResponse(oAuthResponse.getStream(), groupProvider);
      log.debug("Got group entry: {}", group20Entry);
      if (group20Entry == null) {
        return null;
      }
      final List<Group20> group20s = group20Entry.getEntry();
      if (group20s != null && group20s.size() == 1) {
        return group20s.get(0);
      } else if (group20s != null && group20s.size() > 1) {
        throw new RuntimeException(String.format("Received %s groups for groupid %s", group20s.size(), groupId));
      }
    } else {
      log.info("Fetching external group {} for user {} failed with status code {}", groupId, oauth.getPersonId(), oAuthResponse.getCode());
      log.trace(oAuthResponse.getBody());
    }
    return null;
  }

  @Override
  public List<Person> getGroupMembers(GroupProviderUserOauth oauth, GroupProvider provider, String groupId) {
    final GroupMembersEntry groupMembers = this.getGroupMembersEntry(oauth, provider, groupId, MAX_ITEMS, 0);
    return groupMembers == null ? new ArrayList<Person>() : groupMembers.getEntry();
  }

  @Override
  public GroupMembersEntry getGroupMembersEntry(GroupProviderUserOauth oauth, GroupProvider provider, String groupId,
      int limit, int offset) {
    // we assume now that it's a 3-legged oauth provider
    final ThreeLeggedOauth10aGroupProviderApi api = new ThreeLeggedOauth10aGroupProviderApi(provider);
    final GroupProviderServiceThreeLeggedOAuth10a tls = new GroupProviderServiceThreeLeggedOAuth10a(provider, api);
    final OAuthService oAuthService = tls.getOAuthService();

    Token accessToken = new Token(oauth.getOAuthToken(), oauth.getOAuthSecret());
    final String strippedPersonID = convertToExternalPersonId(oauth.getPersonId(), provider);
    final String strippedGroupId = convertToExternalGroupId(groupId, provider);

    OAuthRequest oAuthRequest = getGroupMembersOAuthRequest(provider, api, strippedPersonID, strippedGroupId);

    oAuthRequest.addQuerystringParameter("count", String.valueOf(limit));
    oAuthRequest.addQuerystringParameter("startIndex", String.valueOf(offset));
    oAuthRequest.addQuerystringParameter("sortBy", "familyName");

    oAuthService.signRequest(accessToken, oAuthRequest);

    log.info("About to get group members for group {} with group provider {} using URL: {}", groupId, provider.getIdentifier(), oAuthRequest);

    Response oAuthResponse = oAuthRequest.send();

    if (oAuthResponse.isSuccessful()) {
      GroupMembersEntry entry = getGroupMembersEntryFromResponse(oAuthResponse.getStream(), provider);
      log.debug("Got GroupMembersEntry: {}", entry);
      return entry;
    } else {
      log.info("Fetching external groupmembers for user {} for group {} failed with status code {}",
          strippedPersonID, strippedPersonID, oAuthResponse.getCode());
      log.trace(oAuthResponse.getBody());
    }
    return null;
  }

  private OAuthRequest getGroupListOAuthRequest(GroupProvider provider, final ThreeLeggedOauth10aGroupProviderApi api,
      final String strippedPersonID) {
    OAuthRequest oAuthRequest = new OAuthRequest(api.getRequestTokenVerb(), MessageFormat.format("{0}/groups/{1}",
        provider.getAllowedOptionAsString(GroupProviderOptionParameters.URL), strippedPersonID));
    return oAuthRequest;
  }

  private OAuthRequest getGroupOAuthRequest(GroupProvider provider, final ThreeLeggedOauth10aGroupProviderApi api,
      final String strippedPersonID, final String strippedGroupId) {
    OAuthRequest oAuthRequest = new OAuthRequest(api.getRequestTokenVerb(), MessageFormat.format("{0}/groups/{1}/{2}",
        provider.getAllowedOptionAsString(GroupProviderOptionParameters.URL), strippedPersonID, strippedGroupId));
    return oAuthRequest;
  }

  private OAuthRequest getGroupMembersOAuthRequest(GroupProvider provider,
      final ThreeLeggedOauth10aGroupProviderApi api, final String strippedPersonID, final String strippedGroupId) {
    OAuthRequest oAuthRequest = new OAuthRequest(api.getRequestTokenVerb(), MessageFormat.format("{0}/people/{1}/{2}",
        provider.getAllowedOptionAsString(GroupProviderOptionParameters.URL), strippedPersonID, strippedGroupId));
    return oAuthRequest;
  }

}
