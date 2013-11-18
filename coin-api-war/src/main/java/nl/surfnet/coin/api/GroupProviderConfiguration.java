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
package nl.surfnet.coin.api;

import java.util.List;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.ServiceProviderGroupAcl;

/**
 * Compound strategy to encapsulate all complexity regarding GroupProviders. It
 * is very important that first all GroupProviders are selected and used in
 * subsequent method calls (the signature of the methods demands this also),
 * because we are caching the call getAllGroupProviders.
 * 
 */
public interface GroupProviderConfiguration {

  /*
   * The two Service options for determining the correct ACL
   */
  enum Service {
    People, Group
  }

  /**
   * Verify if the given groupId is from an internal e.g. grouper group
   * 
   * @param groupId
   *          the group identifier
   * @return boolean true id the group is external
   */
  boolean isInternalGroup(String groupId);

  /**
   * Get all GroupProviders that have the correct ACL configured based on the
   * Service. Note that the SP entityId that is used to check against is
   * retrieved from the
   * <code>SecurityContextHolder.getContext().getAuthentication().getPrincipal()</code>
   * context
   * 
   * @param service
   *          the ACL to check for
   * @param spEntityId
   *          the entity of the Service provider
   * @param allGroupProviders
   *          all of the Group Providers
   * @return All GroupProviders who are configured to
   */
  List<GroupProvider> getAllowedGroupProviders(Service service, String spEntityId, List<GroupProvider> allGroupProviders);

  /**
   * Get all group providers
   * 
   * @return all group providers
   */
  List<GroupProvider> getAllGroupProviders();

  /**
   * Is the call allowed? e.g. is there an {@link ServiceProviderGroupAcl} for
   * the given {@link Service} and {@link GroupProvider}
   * 
   * @param service
   *          the ACL to check for
   * @param spEntityId
   *          the entity of the Service provider
   * @param groupProvider the Group Provider
   * @return true if Grouper is present
   */
  boolean isCallAllowed(Service service, String spEntityId, GroupProvider groupProvider);

  /**
   * Gets group members with paginating information for the user's oauth
   * configuration
   * 
   * @param groupProvider
   *          {@link GroupProvider} for the settings
   * @param groupId
   *          the groupId as we know it in SURFconext context (e.g.
   *          urn:collab:group:myuniversity.nl:testgroup)
   * @param limit
   *          maximum number of items
   * @param offset
   *          starting point for paging
   * @return {@link GroupMembersEntry}, can be {@literal null}
   */
  GroupMembersEntry getGroupMembersEntry(GroupProvider groupProvider, String onBehalfOf, String groupId, int limit,
      int offset);

  /**
   * 
   * @param groupProvider
   *          {@link GroupProvider} for the settings
   * @param userId
   *          the userId as we know it in SURFconext context (e.g.
   *          urn:collab:person:myuniversity.nl:testuser)
   * @param limit
   *          maximum number of items
   * @param offset
   *          starting point for paging
   * @return {@link Group20Entry}, can be {@literal null}
   */
  Group20Entry getGroup20Entry(GroupProvider groupProvider, String userId, int limit, int offset);

  /**
   * Strip the urn part off the groupId
   * 
   * @param groupProviders
   *          all groupPrviders
   * @param groupId
   *          the long version with the urn part of the Group
   * @return a GroupId that Grouper understands
   */
  String cutOffUrnPartForGrouper(List<GroupProvider> groupProviders, String groupId);

  /**
   * 
   * Get a single group
   * 
   * @param groupProvider
   *          the GroupProvider
   * @param userId
   *          the userId
   * @param groupId
   *          the groupId
   * @return Group20 can be null
   */
  Group20 getGroup20(GroupProvider groupProvider, String userId, String groupId);

  /**
   * Add to all the groups the conext urn part for grouper
   * 
   * @param groupProviders
   *          all groupProvider
   * @param group20Entry
   *          the group20Entry with Grouper id's
   * @return group20Entry the same group20Entry but now with long conext urn's
   */
  Group20Entry addUrnPartForGrouper(List<GroupProvider> groupProviders, Group20Entry group20Entry);
}