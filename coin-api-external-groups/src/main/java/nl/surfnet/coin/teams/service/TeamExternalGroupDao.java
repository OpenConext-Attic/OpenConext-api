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

import java.util.Collection;
import java.util.List;

import nl.surfnet.coin.teams.domain.ExternalGroup;
import nl.surfnet.coin.teams.domain.TeamExternalGroup;

/**
 * DAO for CRUD operations for the link between a SURFteam and an External Group
 */
public interface TeamExternalGroupDao {

  /**
   * Gets an {@link ExternalGroup} by its SURFconext identifier (urn:collab:groups:univ.nl:nl.univ.mygroup) from the
   * local store
   *
   * @param identifier unique identifier of the external group within the SURFconext platform
   * @return {@link ExternalGroup} that is stored, or {@literal null} if not present
   */
  ExternalGroup getExternalGroupByIdentifier(String identifier);

  /**
   * Gets a List of links between a SURFteam and external groups by the identifier of the SURFteam
   *
   * @param identifier unique identifier of the SURFteam
   * @return List of {@link TeamExternalGroup}, can be empty
   */
  List<TeamExternalGroup> getByTeamIdentifier(String identifier);

  /**
   * Gets a List of links between a SURFteam and external groups by the identifier of the external group
   *
   * @param identifier id of the external group
   * @return List of {@link TeamExternalGroup}, can be empty
   */
  List<TeamExternalGroup> getByExternalGroupIdentifier(String identifier);

  /**
   * Gets a List of links between a SURFteam and external groups by the identifiers of the external groups
   *
   * @param identifiers ids of the external group
   * @return List of {@link TeamExternalGroup}, can be empty
   */
  List<TeamExternalGroup> getByExternalGroupIdentifiers(Collection<String> identifiers);

  /**
   * Gets a specific link between a SURFteam and an external group by their respective SURFconext identifiers
   *
   * @param teamId                  unique identifier of the SURFteam
   * @param externalGroupIdentifier unique identifier of the external group
   * @return {@link TeamExternalGroup} if the link exists, otherwise {@literal null}
   */
  TeamExternalGroup getByTeamIdentifierAndExternalGroupIdentifier(String teamId, String externalGroupIdentifier);

  /**
   * Saves or updates the link between a SURFteam and an external group
   *
   * @param teamExternalGroup {@link TeamExternalGroup} to persist
   */
  void saveOrUpdate(TeamExternalGroup teamExternalGroup);

  /**
   * Deletes the link between a SURFteam and an external group
   *
   * @param teamExternalGroup {@link TeamExternalGroup} to delete
   */
  void delete(TeamExternalGroup teamExternalGroup);
}
