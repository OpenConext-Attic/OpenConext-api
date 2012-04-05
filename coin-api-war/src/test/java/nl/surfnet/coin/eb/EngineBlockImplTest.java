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

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 
 *
 */
public class EngineBlockImplTest {

  private static JdbcTemplate template;
  private static EngineBlockImpl engineBlock;

  /**
   * We use an in-memory database - no need for Spring in this one - and
   * populate it with the sql statements in test-data-eb.sql
   * 
   * @throws Exception
   *           unexpected
   */
  @BeforeClass
  public static void beforeClass() throws Exception {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setPassword("");
    dataSource.setUsername("sa");
    dataSource.setUrl("jdbc:hsqldb:mem:coin");
    dataSource.setDriverClassName("org.hsqldb.jdbcDriver");

    EngineBlockImplTest.template = new JdbcTemplate(dataSource);
    EngineBlockImplTest.engineBlock = new EngineBlockImpl();
    engineBlock.setEbJdbcTemplate(template);
    template.execute(IOUtils.toString(new ClassPathResource("sql/test-data-eb.sql").getInputStream()));

  }

  @AfterClass
  public static void afterClass() throws Exception {
    template.execute(IOUtils.toString(new ClassPathResource("sql/cleanup-test-data-eb.sql").getInputStream()));

  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.eb.EngineBlockImpl#getUserUUID(java.lang.String)}
   * .
   */
  @Test
  public void testGetPersistentNameIdentifier() {
    // user_uuid
    String persistentNameIdentifier = engineBlock.getUserUUID("persistent");
    assertEquals("user_uuid", persistentNameIdentifier);

  }

}
