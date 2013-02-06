/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nl.surfnet.coin.eb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * 
 *
 */
public class EngineBlockImpl implements EngineBlock {

  private static final Logger LOG = LoggerFactory.getLogger(EngineBlockImpl.class);

  @Autowired
  private JdbcTemplate ebJdbcTemplate;

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.eb.EngineBlock#getPersistentNameIdentifier(java.lang.String
   * )
   */
  @Override
  public String getUserUUID(String identifier) {
    Assert.hasText(identifier, "Not allowed to provide a null or empty identifier");
    String sql = "SELECT user_uuid FROM saml_persistent_id WHERE persistent_id =  ?";
    LOG.debug("Executing query with identifier {}: {}", identifier, sql);
    List<String> results = ebJdbcTemplate.query(sql,
     new String[] { identifier }, new RowMapper<String>() {
          @Override
          public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
          }
        });
    if (CollectionUtils.isEmpty(results)) {
      throw new RuntimeException("No persistent_id found for user_uuid("+identifier+")");
    }
    LOG.debug("Numer of results from query: {}", results.size());
    return results.get(0);
  }

  /**
   * @param ebJdbcTemplate the ebJdbcTemplate to set
   */
  public void setEbJdbcTemplate(JdbcTemplate ebJdbcTemplate) {
    this.ebJdbcTemplate = ebJdbcTemplate;
  }

}
