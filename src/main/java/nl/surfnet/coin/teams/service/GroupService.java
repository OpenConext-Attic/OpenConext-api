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
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderUserOauth;

/**
 * Interface to get Group's
 */
public interface GroupService {

  /**
   * Gets a List of {@link Group20}'s for the user's oauth configuration
   *
   * @param oauth {@link GroupProviderUserOauth} configuration for a user
   * @param groupProvider {@link GroupProvider} for the settings
   * @return List of Group20's, can be empty
   */
  List<Group20> getGroup20s(GroupProviderUserOauth oauth, GroupProvider groupProvider);
}
