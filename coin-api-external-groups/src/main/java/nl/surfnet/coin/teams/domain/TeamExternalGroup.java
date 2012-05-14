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

package nl.surfnet.coin.teams.domain;

import nl.surfnet.coin.shared.domain.DomainObject;

/**
 * The link between a team (SURFConext) and external groups from the universities.
 * <p/>
 * * Because the original data behind this bean comes from external sources, we do all CRUD operations by jdbc templates
 * instead of Hibernate.
 * <p/>
 * MySQL query to create this table:
 * <pre>
 *   CREATE TABLE `team_external_groups` (
 * `id` bigint(20) NOT NULL AUTO_INCREMENT,
 * `grouper_team_id` varchar(255) DEFAULT NULL,
 * `external_groups_id` bigint(20) DEFAULT NULL,
 * PRIMARY KEY (`id`),
 * UNIQUE KEY `grouper_team_id` (`grouper_team_id`,`external_groups_id`),
 * KEY `FKB046E6E69AB3B3FA` (`external_groups_id`),
 * CONSTRAINT `FKB046E6E69AB3B3FA` FOREIGN KEY (`external_groups_id`) REFERENCES `external_groups` (`id`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
 * </pre>
 */
public class TeamExternalGroup extends DomainObject {

  private String grouperTeamId;

  private ExternalGroup externalGroup;

  public String getGrouperTeamId() {
    return grouperTeamId;
  }

  public void setGrouperTeamId(String grouperTeamId) {
    this.grouperTeamId = grouperTeamId;
  }

  public ExternalGroup getExternalGroup() {
    return externalGroup;
  }

  public void setExternalGroup(ExternalGroup externalGroup) {
    this.externalGroup = externalGroup;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("TeamExternalGroup");
    sb.append("{id='").append(getId()).append('\'');
    sb.append(", grouperTeamId='").append(grouperTeamId).append('\'');
    sb.append(", externalGroup=").append(externalGroup);
    sb.append('}');
    return sb.toString();
  }
}
