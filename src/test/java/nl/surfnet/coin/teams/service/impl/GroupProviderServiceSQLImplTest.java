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
package nl.surfnet.coin.teams.service.impl;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.List;

import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderUserOauth;
import nl.surfnet.coin.teams.domain.ServiceProviderGroupAcl;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Test for GroupProviderServiceSQLImpl that uses an in-memory database.
 *
 */
public class GroupProviderServiceSQLImplTest {

  private static GroupProviderServiceSQLImpl groupProviderServiceSQL;

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

    JdbcTemplate template = new JdbcTemplate(dataSource);
    groupProviderServiceSQL = new GroupProviderServiceSQLImpl(template);
    template.execute(IOUtils.toString(new ClassPathResource("test-data-eb.sql")
        .getInputStream()));

  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.teams.service.impl.GroupProviderServiceSQLImpl#getGroupProviderUserOauths(java.lang.String)}
   * .
   */
  @Test
  public void testGetGroupProviderUserOauths() {
    List<GroupProviderUserOauth> oauths = groupProviderServiceSQL.getGroupProviderUserOauths("urn:collab:person:test.surfguest.nl:tester");
    assertEquals(1, oauths.size());
    assertEquals("avans", oauths.get(0).getProvider());

  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.teams.service.impl.GroupProviderServiceSQLImpl#getGroupProviderByStringIdentifier(java.lang.String)}
   * .
   */
  @Test
  public void testGetGroupProviderByStringIdentifier() {
      GroupProvider groupProvider = groupProviderServiceSQL.getGroupProviderByStringIdentifier("grouper");
      assertEquals("SURFteams grouper",groupProvider.getName());
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.teams.service.impl.GroupProviderServiceSQLImpl#getOAuthGroupProviders(java.lang.String)}
   * .
   */
  @Test
  public void testGetOAuthGroupProviders() {
    List<GroupProvider> providers = groupProviderServiceSQL.getOAuthGroupProviders("urn:collab:person:test.surfguest.nl:tester");
    assertEquals(1,providers.size());
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.teams.service.impl.GroupProviderServiceSQLImpl#getServiceProviderGroupAcl(java.lang.String)}
   * .
   */
  @Test
  public void testGetServiceProviderGroupAcl() {
    List<ServiceProviderGroupAcl> acls = groupProviderServiceSQL
        .getServiceProviderGroupAcl("https://teams.test.surfconext.nl/shibboleth");
    assertEquals(1, acls.size());
  }

}
