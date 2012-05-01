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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

public class ApiGrouperDaoImpl extends AbstractGrouperDaoImpl implements ApiGrouperDao{

  JdbcTemplate jdbcTemplate;
  
  private static final Map<String, String> VALID_SORTS_FOR_TEAM_QUERY;
  
  static {
    VALID_SORTS_FOR_TEAM_QUERY = new HashMap<String, String>();
    VALID_SORTS_FOR_TEAM_QUERY.put("id", "name");
    VALID_SORTS_FOR_TEAM_QUERY.put("title", "display_name");
    VALID_SORTS_FOR_TEAM_QUERY.put("description", "description");
  }

  public Group20Entry findGroup20(String personId, String groupName) {
    final Group20Entry group20Entry = new Group20Entry(Arrays.asList(jdbcTemplate.queryForObject(SQL_FIND_TEAMS_LIKE_GROUPNAME,
        new Object[]{personId, groupName, 1, 0},
        new OpenSocial20GroupRowMapper())));
    addRolesToGroups(personId, group20Entry.getEntry());
    return group20Entry;
  }

  public Group20Entry findAllGroup20sByMember(String personId, Integer offset, Integer pageSize, String sortBy) {
    int rowCount = this.jdbcTemplate.queryForInt(SQL_FIND_ALL_TEAMS_BY_MEMBER_ROWCOUNT, personId);
    List<Group20> groups = new ArrayList<Group20>();
    pageSize = correctPageSize(pageSize);
    offset = correctOffset(offset);
    try {
      String sql = SQL_FIND_ALL_TEAMS_BY_MEMBER;//SQL_FIND_ALL_TEAMS_BY_MEMBER_SORTED;
      if (StringUtils.isBlank(sortBy) && false) {
        Assert.isTrue(sortBy.equals("name") , "");
        sql = String.format(sql, sortBy);
      }
      groups = jdbcTemplate.query(sql, new Object[]{personId, pageSize, offset},
          new OpenSocial20GroupRowMapper());
      addRolesToGroups(personId, groups);
    } catch (EmptyResultDataAccessException e) {
    }
    return new Group20Entry(groups, pageSize, offset, sortBy, rowCount);

  }

  public static class OpenSocial20GroupRowMapper extends GrouperRowMapper<Group20> {
    @Override
    public Group20 createObj(String id, String name, String description) {
      return new Group20(id, name, description);
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
    protected Map<String, Role> roles;

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
  
  /* (non-Javadoc)
   * @see nl.surfnet.coin.teams.service.impl.ApiGrouperDao#findAllMembers(java.lang.String, int, int)
   */
  @Override
  public GroupMembersEntry findAllMembers(String groupId, Integer offset, Integer pageSize, String sortBy) {
 // TODO: include sortBy in query.
    List<Person> persons = new ArrayList<Person>();
    pageSize = correctPageSize(pageSize);
    offset = correctOffset(offset);
    try {
      RowMapper<Person> mapper = new RowMapper<Person>() {
        @Override
        public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
          Person person = new Person();
          person.setId(rs.getString(1));
          return person;
        }
      };
      persons = jdbcTemplate.query(SQL_MEMBERS_BY_TEAM, new Object[]{groupId, pageSize, offset},
          mapper );
      addPersonRolesToGroup(persons, groupId);
    } catch (EmptyResultDataAccessException e) {
    }
    return new GroupMembersEntry(persons);
  }

  @SuppressWarnings("unchecked")
  private void addPersonRolesToGroup(Collection<Person> persons, String groupId) {
    try {
      RolesMembersRowCallbackHandler handler = new RolesMembersRowCallbackHandler();
      Collection<String> personIds = CollectionUtils.collect(persons, new Transformer() {
        @Override
        public Object transform(Object input) {
          return ((Person)input).getId();
        }
      });
      String join = StringUtils.join(personIds,",");
      this.jdbcTemplate.query(SQL_ROLES_BY_TEAM_AND_MEMBERS, new Object[] { groupId, join }, handler);
      Map<String, Role> roles = handler.roles;
      for (Person person : persons) {
        Role role = roles.get(person.getId());
        role = (role == null ? Role.Member : role);
        person.setVoot_membership_role(role.name().toLowerCase());
      }
    } catch (EmptyResultDataAccessException e) {
      // this we can ignore
    }
  }
  
  private class RolesMembersRowCallbackHandler extends RolesRowCallbackHandler {

    @Override
    public void processRow(ResultSet rs) throws SQLException {
      String personName = rs.getString("subject_id");
      String permission = rs.getString("fieldname");
      Role role = roles.get(personName);
      if (!Role.Admin.equals(role)) {
        roles.put(personName, permission.equals("admins") ? Role.Admin : Role.Manager);
      }
    }
  }
}
