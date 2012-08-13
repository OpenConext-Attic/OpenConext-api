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

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

public abstract class AbstractGrouperDaoImpl  {


  protected static String SQL_FIND_ALL_TEAMS_ROWCOUNT = "select count(distinct gg.name) "
      + "from grouper_groups gg, grouper_stems gs, grouper_members gm, "
      + "grouper_memberships gms, "
      + " grouper_fields gf, grouper_group_set ggs  "
      + "where gg.parent_stem = gs.id and gms.member_id = gm.id and gms.owner_group_id = gg.id "
      + " and gs.name != 'etc' "
      + "and ggs.field_id = gf.id "
      + " and gg.id = ggs.owner_group_id "
      + "and gms.owner_id = ggs.member_id "
      + " and gms.field_id = ggs.member_field_id "
      + "and ((gf.type = 'access' and gf.name = 'viewers') or gm.subject_id = ?) ";
  protected static String SQL_FIND_ALL_TEAMS = "select distinct gg.name, gg.display_name ,gg.description, "
      + "gs.name as stem_name, gs.display_name as stem_display_name, gs.description as stem_description "
      + "from grouper_groups gg, grouper_stems gs, grouper_members gm, "
      + "grouper_memberships gms, "
      + " grouper_fields gf, grouper_group_set ggs  "
      + "where gg.parent_stem = gs.id and gms.member_id = gm.id and gms.owner_group_id = gg.id "
      + " and gs.name != 'etc' "
      + " and ggs.field_id = gf.id "
      + " and gg.id = ggs.owner_group_id "
      + "and gms.owner_id = ggs.member_id "
      + " and gms.field_id = ggs.member_field_id "
      + "and ((gf.type = 'access' and gf.name = 'viewers') or gm.subject_id = ?) "
      + "order by gg.name limit ? offset ?";

  protected static String SQL_FIND_TEAM_BY_MEMBER_AND_BY_GROUPNAME =
      "select distinct gg.name, gg.display_name ,gg.description, gs.name as stem_name, "
          + "gs.display_name as stem_display_name, gs.description as stem_description "
          + "from grouper_groups gg, grouper_stems gs, grouper_members gm,"
          + "grouper_memberships gms, "
          + " grouper_fields gf, grouper_group_set ggs  "
          + "where gg.parent_stem = gs.id and gms.member_id = gm.id and gms.owner_group_id = gg.id "
          + " and gs.name != 'etc' "
          + " and ggs.field_id = gf.id "
          + " and gg.id = ggs.owner_group_id "
          + "and gms.owner_id = ggs.member_id "
          + " and gms.field_id = ggs.member_field_id "
          + "and (gm.subject_id = ?) "
          + "and upper(gg.name) = ?";
  protected static String SQL_FIND_ALL_TEAMS_BY_MEMBER_ROWCOUNT = "select count(distinct gg.name) from grouper_groups gg, grouper_stems gs, grouper_members gm, "
      + "grouper_memberships gms "
      + "where gg.parent_stem = gs.id and gms.member_id = gm.id and gms.owner_group_id = gg.id "
      + "and gm.subject_id = ? and gs.name != 'etc'";
  protected static String SQL_FIND_ALL_TEAMS_BY_MEMBER = "select distinct gg.name, gg.display_name ,gg.description, gs.name as stem_name, gs.display_name as stem_display_name, gs.description as stem_description "
      + "from grouper_groups gg, grouper_stems gs, grouper_members gm, "
      + "grouper_memberships gms, grouper_fields gf "
      + "where gg.parent_stem = gs.id and gms.member_id = gm.id and gms.owner_group_id = gg.id "
      + "and gm.subject_id = ? "
      + "and gs.name != 'etc' "
      + "and gf.id = gms.field_id and gf.name = 'members' "
      + "order by gg.name limit ? offset ?";
  protected static String SQL_FIND_ALL_TEAMS_BY_MEMBER_SORTED = "select distinct gg.name, gg.display_name ,gg.description, gs.name as stem_name, gs.display_name as stem_display_name, gs.description as stem_description "
      + "from grouper_groups gg, grouper_stems gs, grouper_members gm, "
      + "grouper_memberships gms, grouper_fields gf "
      + "where gg.parent_stem = gs.id and gms.member_id = gm.id and gms.owner_group_id = gg.id "
      + "and gm.subject_id = ? "
      + "and gs.name != 'etc' "
      + "and gf.id = gms.field_id and gf.name = 'members' "
      + "order by gg.%s limit ? offset ?";
  protected static String SQL_FIND_TEAMS_BY_MEMBER_ROWCOUNT = "select count(distinct gg.name) "
      + "from grouper_groups gg, grouper_stems gs, grouper_members gm, "
      + "grouper_memberships gms "
      + "where gg.parent_stem = gs.id and gms.member_id = gm.id and gms.owner_group_id = gg.id "
      + "and gm.subject_id = ?  and upper(gg.name) like ?";
  protected static String SQL_FIND_TEAMS_BY_MEMBER = "select distinct gg.name, gg.display_name ,gg.description, gs.name as stem_name, gs.display_name as stem_display_name, gs.description as stem_description "
      + "from grouper_groups gg, grouper_stems gs, grouper_members gm, "
      + "grouper_memberships gms, grouper_fields gf "
      + "where gg.parent_stem = gs.id and gms.member_id = gm.id and gms.owner_group_id = gg.id "
      + "and gm.subject_id = ? "
      + "and gs.name != 'etc' "
      + "and gf.id = gms.field_id and gf.name = 'members' "
      + "and upper(gg.name) like ? order by gg.name limit ? offset ?";
  protected static String SQL_FIND_STEMS_BY_MEMBER = "select distinct gs.name, gs.display_name, gs.description "
      + "from grouper_groups gg, grouper_stems gs, grouper_members gm, "
      + "grouper_memberships gms  "
      + "where gg.parent_stem = gs.id and gms.member_id = gm.id and gms.owner_group_id = gg.id "
      + "and gm.subject_id = ? "
      + "and gs.name != 'etc' ";

  protected static final String SQL_ROLES_BY_TEAMS = " select gf.name as fieldname, " +
      "gg.name as groupname from grouper_memberships gms, "
      + "grouper_groups gg, grouper_fields gf, "
      + " grouper_stems gs, grouper_members gm where "
      + " gms.field_id = gf.id and  gms.owner_group_id = gg.id and "
      + " gms.member_id = gm.id "
      + " and gm.subject_id = ?  "
      + " and gg.parent_stem = gs.id "
      + " and gs.name != 'etc' "
      + " and (gf.name = 'admins' or gf.name = 'updaters') order by gg.name ";

  /**
   * Pad a string with SQL wildcards
   * @param part the string to search for
   * @return padded string
   */
  protected String wildCard(String part) {
    Assert.hasText(part);
    part = ("%" + part + "%").toUpperCase();
    return part;
  }
  protected static final String SQL_ROLES_BY_TEAM_AND_MEMBERS = "select gm.subject_id as subject_id, " +
  		"gf.name as fieldname, gg.name as groupname from grouper_memberships gms, " +
  		"grouper_groups gg, grouper_fields gf, grouper_stems gs, grouper_members gm " +
  		"where gms.field_id = gf.id and  gms.owner_group_id = gg.id and gms.member_id = gm.id " +
  		"and gg.parent_stem = gs.id and gs.name != 'etc' and subject_id in (:identifiers) " +
  		"and (gf.name = 'admins' or gf.name = 'updaters') and gg.name = :groupId";

  protected static final String SQL_MEMBERS_BY_TEAM = " select distinct gm.subject_id as subject_id " +
  		"from grouper_memberships gms, grouper_groups gg, grouper_stems gs, " +
  		"grouper_members gm where gms.owner_group_id = gg.id and gms.member_id = gm.id " +
  		"and gg.parent_stem = gs.id and gs.name != 'etc' and gm.subject_id != 'GrouperSystem' " +
  		"and gm.subject_id != 'GrouperAll' and gg.name = ? order by gm.subject_id limit ? offset ?";

  protected static final String SQL_ADD_MEMBER_COUNT_TO_TEAMS = "select gg.name  as groupname, " +
      "count(distinct gms.member_id) as membercount from "
      + " grouper_groups gg, grouper_stems gs, grouper_members gm, "
      + " grouper_memberships gms "
      + " where gg.parent_stem = gs.id and gms.member_id = gm.id and gms.owner_group_id = gg.id "
      + " and gm.subject_type = 'person' "
      + " and gs.name != 'etc' "
      + " and gg.id in (select distinct(ggo.id) from grouper_groups ggo, grouper_members gmo, grouper_memberships gmso  "
      + " where gmso.member_id = gmo.id and gmso.owner_group_id = ggo.id and gmo.subject_id = ?)   "
      + " group by gg.name  ";

  protected static String SQL_FIND_TEAMS_LIKE_GROUPNAMES_ROWCOUNT =
      "select count(distinct gg.name) as groupcount "
          + "from grouper_groups gg, grouper_stems gs, grouper_members gm,"
          + "grouper_memberships gms, "
          + " grouper_fields gf, grouper_group_set ggs  "
          + "where gg.parent_stem = gs.id and gms.member_id = gm.id and gms.owner_group_id = gg.id "
          + " and gs.name != 'etc' "
          + " and ggs.field_id = gf.id "
          + " and gg.id = ggs.owner_group_id "
          + "and gms.owner_id = ggs.member_id "
          + " and gms.field_id = ggs.member_field_id "
          + "and gg.name in (:groupId)";

  protected static String SQL_FIND_TEAMS_LIKE_GROUPNAMES =
      "select distinct gg.name, gg.display_name ,gg.description, gs.name as stem_name, "
          + "gs.display_name as stem_display_name, gs.description as stem_description "
          + "from grouper_groups gg, grouper_stems gs, grouper_members gm,"
          + "grouper_memberships gms, "
          + " grouper_fields gf, grouper_group_set ggs  "
          + "where gg.parent_stem = gs.id and gms.member_id = gm.id and gms.owner_group_id = gg.id "
          + " and gs.name != 'etc' "
          + " and ggs.field_id = gf.id "
          + " and gg.id = ggs.owner_group_id "
          + "and gms.owner_id = ggs.member_id "
          + " and gms.field_id = ggs.member_field_id "
          + "and gg.name in (:groupId) order by gg.name limit :limit offset :offset";

  /**
   * Template method Row Mapper that only extracts the fields from the resultset, leaving creation
   *  of a concrete group to implementations.
   * @param <T> the group class to create.
   */
  public abstract static class GrouperRowMapper<T> implements RowMapper<T> {

    public abstract T createObj(String id, String name, String description);

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {

      String id = rs.getString("name");
      String name = rs.getString("display_name");
      name = name.substring(name.lastIndexOf(':') + 1);
      String description = rs.getString("description");
      return createObj(id, name, description);
    }
  }
  protected Integer correctOffset(Integer offset) {
    if (offset == null) {
      offset = new Integer(0);
    }
    return offset;
  }

  protected Integer correctPageSize(Integer pageSize) {
    if (pageSize == null || pageSize.intValue() == 0) {
      pageSize = Integer.MAX_VALUE;
    }
    return pageSize;
  }

}
