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
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;

public class ApiGrouperDaoImpl extends AbstractGrouperDaoImpl implements ApiGrouperDao {

  private JdbcTemplate jdbcTemplate;
  /*
   * http://static.springsource.org/spring/docs/2.5.x/reference/jdbc.html#jdbc-in
   * -clause
   */
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  private static final Map<String, String> VALID_SORTS_FOR_TEAM_QUERY;

  static {
    VALID_SORTS_FOR_TEAM_QUERY = new HashMap<String, String>();
    VALID_SORTS_FOR_TEAM_QUERY.put("id", "name");
    VALID_SORTS_FOR_TEAM_QUERY.put("title", "display_name");
    VALID_SORTS_FOR_TEAM_QUERY.put("description", "description");
  }

  public Group20Entry findGroup20(String personId, String groupName) {
    Group20Entry group20Entry;
    Assert.notNull(personId, "The personId can not be null");
    Assert.notNull(groupName, "The groupName can not be null");
    try {
      group20Entry = new Group20Entry(Arrays.asList(jdbcTemplate.queryForObject(
          SQL_FIND_TEAM_BY_MEMBER_AND_BY_GROUPNAME, new Object[] { personId, groupName.toUpperCase() },
          new OpenSocial20GroupRowMapper())));
      addRolesToGroups(personId, group20Entry.getEntry());
    } catch (EmptyResultDataAccessException ignored) {
      group20Entry = new Group20Entry();
    }
    return group20Entry;
  }

  public Group20Entry findAllGroup20sByMember(String personId, Integer offset, Integer pageSize, String sortBy) {
    int rowCount = this.jdbcTemplate.queryForInt(SQL_FIND_ALL_TEAMS_BY_MEMBER_ROWCOUNT, personId);
    List<Group20> groups = new ArrayList<Group20>();
    pageSize = correctPageSize(pageSize);
    offset = correctOffset(offset);
    try {
      String sql = formatAllTeamsSQLWithSortByOption(sortBy);
      groups = jdbcTemplate.query(sql, new Object[] { personId, pageSize, offset }, new OpenSocial20GroupRowMapper());
      addRolesToGroups(personId, groups);
    } catch (EmptyResultDataAccessException e) {
    }
    return new Group20Entry(groups, pageSize, offset, sortBy, rowCount);

  }

  protected String formatAllTeamsSQLWithSortByOption(String sortBy) {
    String sql = SQL_FIND_ALL_TEAMS_BY_MEMBER_SORTED;
    if (!StringUtils.isBlank(sortBy)) {
      String sortByColumn = null;
      Set<Entry<String, String>> entrySet = VALID_SORTS_FOR_TEAM_QUERY.entrySet();
      for (Entry<String, String> entry : entrySet) {
        if (entry.getKey().equals(sortBy)) {
          sortByColumn = entry.getValue();
          break;
        }
      }
      Assert.isTrue(!StringUtils.isBlank(sortByColumn), "The only supported sortBy options are ("
          + VALID_SORTS_FOR_TEAM_QUERY.keySet() + "). Not allowed is '" + sortBy + "'");
      sql = String.format(sql, sortByColumn);
    } else {
      sql = String.format(sql, "name");
    }
    return sql;
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.teams.service.impl.ApiGrouperDao#findAllMembers(java.lang
   * .String, int, int)
   */
  @Override
  public GroupMembersEntry findAllMembers(String groupId, Integer offset, Integer pageSize) {
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
      persons = jdbcTemplate.query(SQL_MEMBERS_BY_TEAM, new Object[] { groupId, pageSize, offset }, mapper);
      if (CollectionUtils.isNotEmpty(persons)) {
        addPersonRolesToGroup(persons, groupId);
      }
    } catch (EmptyResultDataAccessException e) {
      // ignore as we have a sensible default
    }
    return new GroupMembersEntry(persons);
  }

  @Override
  public Group20Entry findGroups20ByIds(String personId, String[] groupIds, Integer pageSize, Integer offset) {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("groupId", Arrays.asList(groupIds));

    List<Group20> groups = new ArrayList<Group20>();
    pageSize = correctPageSize(pageSize);
    offset = correctOffset(offset);
    params.put("limit", pageSize);
    params.put("offset", offset);
    try {
      String sql = SQL_FIND_TEAMS_LIKE_GROUPNAMES;
      groups = namedParameterJdbcTemplate.query(sql, params, new OpenSocial20GroupRowMapper());
      addRolesToGroups(personId, groups);
    } catch (EmptyResultDataAccessException e) {
    }
    return new Group20Entry(groups, pageSize, offset, null, groups.size());
  }

  @SuppressWarnings("unchecked")
  private void addPersonRolesToGroup(Collection<Person> persons, String groupId) {
    try {
      RolesMembersRowCallbackHandler handler = new RolesMembersRowCallbackHandler();
      Collection<String> personIds = CollectionUtils.collect(persons, new Transformer() {
        @Override
        public Object transform(Object input) {
          return ((Person) input).getId();
        }
      });
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("groupId", groupId);
      params.put("identifiers", personIds);
      namedParameterJdbcTemplate.query(SQL_ROLES_BY_TEAM_AND_MEMBERS, params, handler);
      for (Person person : persons) {
        Role role = handler.roles.get(person.getId());
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

  /**
   * @param namedParameterJdbcTemplate
   *          the namedParameterJdbcTemplate to set
   */
  public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }
}
