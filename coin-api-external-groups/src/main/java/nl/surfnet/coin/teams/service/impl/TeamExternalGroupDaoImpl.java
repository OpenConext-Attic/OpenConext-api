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
import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import nl.surfnet.coin.teams.domain.ExternalGroup;
import nl.surfnet.coin.teams.domain.TeamExternalGroup;
import nl.surfnet.coin.teams.service.TeamExternalGroupDao;

/**
 * SQL Implementation for {@link TeamExternalGroupDao}
 */
@Component("teamExternalGroupDao")
public class TeamExternalGroupDaoImpl implements TeamExternalGroupDao {

  private static final String IDENTIFIER = "identifier";
  private static final String DESCRIPTION = "description";
  private static final String NAME = "name";
  private static final String GROUP_PROVIDER = "group_provider";
  private static final String GROUPER_TEAM_ID = "grouper_team_id";

  @Resource(name = "grouperJdbcTemplate")
  private JdbcTemplate jdbcTemplate;

  public void setJdbcTemplate(JdbcTemplate tpl) {
    this.jdbcTemplate = tpl;
  }

  @Override
  public ExternalGroup getExternalGroupByIdentifier(String identifier) {
    Object[] args = {identifier};

    try {
      return this.jdbcTemplate.queryForObject("SELECT * FROM external_groups AS eg WHERE eg.identifier = ?",
          args, new RowMapper<ExternalGroup>() {
        @Override
        public ExternalGroup mapRow(ResultSet rs, int rowNum) throws SQLException {

          final ExternalGroup e = new ExternalGroup();
          e.setId(rs.getLong("id"));
          e.setIdentifier(rs.getString(IDENTIFIER));
          e.setDescription(rs.getString(DESCRIPTION));
          e.setName(rs.getString(NAME));
          e.setGroupProviderIdentifier(rs.getString(GROUP_PROVIDER));
          return e;
        }
      });
    } catch (EmptyResultDataAccessException er) {
      return null;
    }
  }

  @Override
  public List<TeamExternalGroup> getByTeamIdentifier(String identifier) {
    Object[] args = {identifier};

    try {
      String s = "SELECT teg.id AS teg_id, teg.grouper_team_id, eg.id AS eg_id, eg.identifier, eg.name, eg.description, eg.group_provider " +
          "          FROM team_external_groups AS teg " +
          "          INNER JOIN external_groups AS eg " +
          "          ON teg.external_groups_id = eg.id " +
          "          WHERE teg.grouper_team_id = ? ";
      return this.jdbcTemplate.query(s, args, new RowMapper<TeamExternalGroup>() {
        @Override
        public TeamExternalGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
          return mapRowToTeamExternalGroup(rs);
        }
      });
    } catch (EmptyResultDataAccessException er) {
      return null;
    }
  }

  @Override
  public List<TeamExternalGroup> getByExternalGroupIdentifier(String identifier) {
    Object[] args = {identifier};

    try {
      String s = "SELECT teg.id AS teg_id, teg.grouper_team_id, eg.id AS eg_id, eg.identifier, eg.name, eg.description, eg.group_provider " +
          "          FROM team_external_groups AS teg " +
          "          INNER JOIN external_groups AS eg " +
          "          ON teg.external_groups_id = eg.id " +
          "          WHERE eg.identifier = ? ";
      return this.jdbcTemplate.query(s, args, new RowMapper<TeamExternalGroup>() {
        @Override
        public TeamExternalGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
          return mapRowToTeamExternalGroup(rs);
        }
      });
    } catch (EmptyResultDataAccessException er) {
      return null;
    }
  }

  @Override
  public TeamExternalGroup getByTeamIdentifierAndExternalGroupIdentifier(String teamId, String externalGroupIdentifier) {
    Object[] args = {teamId, externalGroupIdentifier};
    String s = "SELECT teg.id AS teg_id, teg.grouper_team_id, eg.id AS eg_id, eg.identifier, eg.name, eg.description, eg.group_provider " +
        "          FROM team_external_groups AS teg " +
        "          INNER JOIN external_groups AS eg " +
        "          ON teg.external_groups_id = eg.id " +
        "          WHERE teg.grouper_team_id = ? AND eg.identifier = ?";
    try {
      return this.jdbcTemplate.queryForObject(s, args, new RowMapper<TeamExternalGroup>() {
        @Override
        public TeamExternalGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
          return mapRowToTeamExternalGroup(rs);
        }
      });
    } catch (EmptyResultDataAccessException er) {
      return null;
    }
  }

  private TeamExternalGroup mapRowToTeamExternalGroup(ResultSet rs) throws SQLException {
    ExternalGroup e = new ExternalGroup();
    e.setId(rs.getLong("eg_id"));
    e.setIdentifier(rs.getString(IDENTIFIER));
    e.setDescription(rs.getString(DESCRIPTION));
    e.setName(rs.getString(NAME));
    e.setGroupProviderIdentifier(rs.getString(GROUP_PROVIDER));

    TeamExternalGroup teg = new TeamExternalGroup();
    teg.setId(rs.getLong("teg_id"));
    teg.setGrouperTeamId(rs.getString(GROUPER_TEAM_ID));
    teg.setExternalGroup(e);
    return teg;
  }

  @Override
  public void saveOrUpdate(TeamExternalGroup teamExternalGroup) {
    final TeamExternalGroup storedTeg =
        getByTeamIdentifierAndExternalGroupIdentifier(teamExternalGroup.getGrouperTeamId(),
            teamExternalGroup.getExternalGroup().getIdentifier());
    if (storedTeg != null) {
      updateExternalGroupValues(storedTeg.getExternalGroup(), teamExternalGroup.getExternalGroup());
      return;
    }

    ExternalGroup storedEg = getExternalGroupByIdentifier(teamExternalGroup.getExternalGroup().getIdentifier());
    if (storedEg == null) {
      insertExternalGroup(teamExternalGroup.getExternalGroup());
    } else {
      updateExternalGroupValues(storedEg, teamExternalGroup.getExternalGroup());
    }
    storedEg = getExternalGroupByIdentifier(teamExternalGroup.getExternalGroup().getIdentifier());
    teamExternalGroup.setExternalGroup(storedEg);

    Object[] args = {teamExternalGroup.getGrouperTeamId(), teamExternalGroup.getExternalGroup().getId()};
    String s = "INSERT INTO team_external_groups(grouper_team_id, external_groups_id) VALUES (?, ?);";
    this.jdbcTemplate.update(s, args);
  }

  private void insertExternalGroup(ExternalGroup externalGroup) {
    Object[] args = {externalGroup.getDescription(), externalGroup.getGroupProviderIdentifier(),
        externalGroup.getIdentifier(), externalGroup.getName()};

    String s = "INSERT INTO external_groups (description, group_provider, identifier, name) VALUES (?, ?, ?, ?);";
    this.jdbcTemplate.update(s, args);
  }

  private void updateExternalGroupValues(ExternalGroup existing, ExternalGroup newEG) {
    Object[] newVals = {newEG.getName(), newEG.getDescription(), existing.getId()};
    this.jdbcTemplate.update("UPDATE external_groups SET name = ?, description = ? WHERE id = ?", newVals);
  }

  @Override
  public void delete(TeamExternalGroup teamExternalGroup) {
    Object[] args = {teamExternalGroup.getId()};
    final int deleted = this.jdbcTemplate.update("DELETE FROM team_external_groups WHERE id = ?;", args);

    if (deleted == 0) {
      return;
    }

    args[0] = teamExternalGroup.getExternalGroup().getId();

    final int linksToExternalGroup = this.jdbcTemplate.queryForInt(
        "SELECT COUNT(id) FROM team_external_groups WHERE external_groups_id = ?;", args);
    if (linksToExternalGroup == 0) {
      this.jdbcTemplate.update("DELETE FROM external_groups WHERE id = ?;", args);
    }
  }
}
