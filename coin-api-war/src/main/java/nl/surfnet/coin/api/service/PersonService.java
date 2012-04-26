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


import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.domain.PersonEntry;

public interface PersonService {

  /**
   * Get the OpenSocial Person. Note that the onBehalfOf can be null.
   *
   * @param userId     the unique identifier
   * @param onBehalfOf the unique identifier of the user that is going to make the
   *                   request
   * @return the {@link PersonEntry}
   */
  @PreAuthorize("#onBehalfOf == null or #userId.equals(#onBehalfOf)")
  PersonEntry getPerson(String userId, String onBehalfOf);

  /**
   * Get the group members of the given group. Note that the onBehalfOf can be null.
   *
   * @param groupId    {@link String} the unique identifier for the group
   * @param onBehalfOf {@link String} the unique identifier of the user that is going to
   *                   make the request
   * @param count      nr of records to fetch
   * @param startIndex first record nr.
   * @param sortBy     field to sort by.
   * @return an {@link java.util.ArrayList} containing {@link Person}'s
   */
  @PostAuthorize("#onBehalf == null or returnObject.isMember(#onBehalfOf)")
  GroupMembersEntry getGroupMembers(String groupId, String onBehalfOf, Integer count, Integer startIndex,
                                    String sortBy);
}
