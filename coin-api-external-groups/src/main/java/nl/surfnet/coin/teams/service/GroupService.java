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

package nl.surfnet.coin.teams.service;

import java.util.List;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderUserOauth;

/**
 * Interface to get Group's
 */
public interface GroupService {

  /**
   * Gets a {@link Group20Entry} for the user's oauth configuration
   *
   * @param oauth         {@link nl.surfnet.coin.teams.domain.GroupProviderUserOauth} configuration for a user
   * @param groupProvider {@link nl.surfnet.coin.teams.domain.GroupProvider} for the settings
   * @param limit         maximum amount of {@link Group20}'s in the resultset
   * @param offset        start index
   * @return {@link Group20Entry}
   */
  Group20Entry getGroup20Entry(GroupProviderUserOauth oauth, GroupProvider groupProvider, int limit, int offset);

  /**
   * Gets a List of {@link Group20}'s for the user's oauth configuration
   *
   * @param oauth         {@link GroupProviderUserOauth} configuration for a user
   * @param groupProvider {@link GroupProvider} for the settings
   * @return List of Group20's, can be empty
   */
  List<Group20> getGroup20List(GroupProviderUserOauth oauth, GroupProvider groupProvider);

  /**
   * Gets a specific {@link Group20} for the users oauth configuration
   *
   * @param oauth         {@link GroupProviderUserOauth} configuration for a user
   * @param groupProvider {@link GroupProvider} for the settings
   * @param groupId       identifier of the external group
   * @return {@link Group20}, can be {@literal null}
   */
  Group20 getGroup20(GroupProviderUserOauth oauth, GroupProvider groupProvider, String groupId);


  /**
   * Gets a List of  for the user's oauth configuration
   *
   * @param oauth         {@link GroupProviderUserOauth} configuration for a user
   * @param groupProvider {@link GroupProvider} for the settings
   * @param groupId       the groupId as we know it in SURFconext context (e.g. urn:collab:group:myuniversity.nl:testgroup)
   * @return List of Group20's, can be empty
   * @deprecated use {@link #getGroupMembersEntry}
   */
  @Deprecated
  List<Person> getGroupMembers(GroupProviderUserOauth oauth, GroupProvider groupProvider, String groupId);

  /**
   * Gets group members with paginating information for the user's oauth configuration
   *
   * @param oauth         {@link GroupProviderUserOauth} configuration for a user
   * @param groupProvider {@link GroupProvider} for the settings
   * @param groupId       the groupId as we know it in SURFconext context (e.g. urn:collab:group:myuniversity.nl:testgroup)
   * @param limit         maximum number of items
   * @param offset        starting point for paging
   * @return {@link GroupMembersEntry}, can be {@literal null}
   */
  GroupMembersEntry getGroupMembersEntry(GroupProviderUserOauth oauth, GroupProvider groupProvider, String groupId,
                                         int limit, int offset);
}
