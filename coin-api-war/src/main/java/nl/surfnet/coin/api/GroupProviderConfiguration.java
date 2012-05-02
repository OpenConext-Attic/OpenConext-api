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
import java.util.regex.Pattern;

import nl.surfnet.coin.teams.domain.GroupProvider;

import org.springframework.cache.annotation.Cacheable;

/**
 * Utility class to encapsulate all complexity regarding GroupProviders. 
 *
 */
public interface GroupProviderConfiguration {

  Pattern INTERNAL_GROUP_PATTERN = Pattern.compile("^urn:collab:group:\\w*\\.?surfteams.nl.*");

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
   * @param Service
   *          the ACL to check for
   * @return All GroupProviders who are configured to
   */
  List<GroupProvider> getAllowedGroupProviders(Service service, String spEntityId);

  /**
   * Get all group providers
   * 
   * @return all group providers
   */
  @Cacheable(value = { "group-providers" }, key = "all-group-providers")
  List<GroupProvider> getAllGroupProviders();

  /**
   * We make the distinction between external group providers and the internal
   * SURFteams groups
   * 
   * @param all of the GroupProviders that have valid acl's
   * @return true if Grouper is present
   */
  boolean isGrouperCallsAllowed(List<GroupProvider> groupProviders);

}