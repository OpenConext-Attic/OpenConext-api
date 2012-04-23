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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;

public class ApiGrouperDaoImpl extends AbstractGrouperDaoImpl {

  JdbcTemplate jdbcTemplate;

  public Group20Entry findAllGroup20sByMember(String personId, int offset, int pageSize) {
    //    int rowCount = this.jdbcTemplate.queryForInt(SQL_FIND_ALL_TEAMS_BY_MEMBER_ROWCOUNT, personId);
    List<Group20> groups = new ArrayList<Group20>();
    try {
      groups = jdbcTemplate.query(SQL_FIND_ALL_TEAMS_BY_MEMBER, new Object[]{personId, pageSize, offset},
          new OpenSocial20GroupRowMapper());
      addRolesToGroups(personId, groups);
    } catch (EmptyResultDataAccessException e) {
    }
    return new Group20Entry(groups);

  }

  public static class OpenSocial20GroupRowMapper extends GrouperRowMapper<Group20> {
    @Override
    public Group20 createObj(String id, String name, String description, String vootMembershipRole) {
      return new Group20(id, name, description, vootMembershipRole);
    }
  }

  private enum Role {
    Manager, Admin, Member, none
  }

  private void addRolesToGroups(String personId, List<Group20> groups) {
    try {
      RolesRowCallbackHandler handler = new RolesRowCallbackHandler();
      this.jdbcTemplate.query(SQL_ROLES_BY_TEAMS, new Object[] { personId }, handler);
      Map<String, Role> roles = handler.roles;
      for (Group20 group : groups) {
        Role role = roles.get(group.getId());
        role = (role == null ? Role.Member : role);

        group.setVoot_membership_role(role.name().toLowerCase());
      }
    } catch (EmptyResultDataAccessException e) {
      // this we can ignore
    }

  }
  private class RolesRowCallbackHandler implements RowCallbackHandler {
    private Map<String, Role> roles;

    public RolesRowCallbackHandler() {
      super();
      this.roles = new HashMap<String, Role>();
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
      String groupName = rs.getString("groupname");
      String permission = rs.getString("fieldname");
      /*
       * If the permission equals 'admins' then we have an Role.Admin, else we
       * have a role Role.Manager, but we must not overwrite a previous
       * Role.Admin
       */
      Role role = roles.get(groupName);
      if (!Role.Admin.equals(role)) {
        roles.put(groupName, permission.equals("admins") ? Role.Admin : Role.Manager);
      }
    }
  }

  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }
}
