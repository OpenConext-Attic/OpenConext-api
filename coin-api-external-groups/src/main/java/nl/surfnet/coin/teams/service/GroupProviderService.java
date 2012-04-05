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

import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderUserOauth;
import nl.surfnet.coin.teams.domain.ServiceProviderGroupAcl;

/**
 * Interface to get Group providers
 */
public interface GroupProviderService {

  /**
   * Gets a List of {@link GroupProviderUserOauth}'s for a given user
   *
   * @param userId unique identifier of the user
   * @return List of GroupProviderUserOauth, can be empty
   */
  List<GroupProviderUserOauth> getGroupProviderUserOauths(String userId);

  /**
   * Gets a {@link GroupProvider} by its String identifier
   *
   * @param identifier String identifier of the GroupProvider
   * @return {@link GroupProvider} if it exists, otherwise null
   */
  GroupProvider getGroupProviderByStringIdentifier(String identifier);

  /**
   * Gets a List of {@link GroupProvider}'s for a given user
   *
   * @param userId unique identifier of the user
   * @return List of GroupProvider's, can be empty
   */
  List<GroupProvider> getOAuthGroupProviders(String userId);
  
  
  /**
   * 
   * @param String Service Provider entityId
   * @return All ServiceProviderGroupAcl's for a specific Service Provider
   */
  List<ServiceProviderGroupAcl> getServiceProviderGroupAcl(String serviceProviderEntityId);
}
