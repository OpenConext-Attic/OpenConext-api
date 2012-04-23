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

package nl.surfnet.coin.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupEntry;

/**
 *  GroupService responsible for retrieving groups
 *
 */
public interface GroupService {

  /**
   * Get Persons' Groups
   * 
   * @param userId
   *          the unique identifier
   * @param onBehalfOf
   *          the unique identifier of the user that is going to make the
   *          request
   * @return {@link List} containing the {@link Group}s
   */
  @PreAuthorize("#onBehalfOf == null or #userId.equals(#onBehalfOf)")
  GroupEntry getGroups(String userId, String onBehalfOf);
  
  /**
   * Get Persons' Groups
   * 
   * @param userId
   *          the unique identifier
   * @param onBehalfOf
   *          the unique identifier of the user that is going to make the
   *          request
   * @return {@link List} containing the {@link Group}s
   */
  @PreAuthorize("#onBehalfOf == null or #userId.equals(#onBehalfOf)")
  Group20Entry getGroups20(String userId, String onBehalfOf);

}
