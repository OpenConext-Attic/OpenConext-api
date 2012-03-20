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
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.service.GroupProviderService;

/**
 * SQL implementation of {@link GroupProviderService}
 */
public class GroupProviderServiceSQLImpl implements GroupProviderService {

  // Cannot autowire because OpenConext-teams already has a JdbcTemplate defined for Grouper
  private final JdbcTemplate jdbcTemplate;

  public GroupProviderServiceSQLImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<GroupProvider> getGroupProviders(String userId) {
    Object[] args = new Object[1];
    args[0] = userId;

    try {
      return this.jdbcTemplate.query(
          "SELECT gp.id, gp.identifier, gp.name, gp.classname " +
          "FROM group_provider AS gp " +
          "WHERE gp.identifier = " +
          "    (SELECT gp_user_oauth.`provider_id` " +
          "     FROM group_provider_user_oauth as gp_user_oauth " +
          "     WHERE gp_user_oauth.`user_id` = ?);", args, new RowMapper<GroupProvider>() {
        @Override
        public GroupProvider mapRow(ResultSet rs, int rowNum) throws SQLException {
          Long id = rs.getLong("id");
          String identifier = rs.getString("identifier");
          String name = rs.getString("name");
          String gpClassName = rs.getString("classname");
          return new GroupProvider(id, identifier, name, gpClassName);
        }
      });
    } catch (EmptyResultDataAccessException e) {
      return new ArrayList<GroupProvider>();
    }
  }
}
