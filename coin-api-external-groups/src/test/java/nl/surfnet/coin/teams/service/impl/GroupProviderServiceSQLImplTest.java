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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nl.surfnet.coin.db.AbstractInMemoryDatabaseTest;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderType;
import nl.surfnet.coin.teams.domain.GroupProviderUserOauth;
import nl.surfnet.coin.teams.domain.ServiceProviderGroupAcl;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GroupProviderServiceSQLImpl that uses an in-memory database.
 *
 */
public class GroupProviderServiceSQLImplTest extends AbstractInMemoryDatabaseTest {

  private static GroupProviderServiceSQLImpl groupProviderServiceSQL;

  @Before
  public void init() {
    groupProviderServiceSQL = new GroupProviderServiceSQLImpl(super.getJdbcTemplate());
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
    GroupProviderUserOauth groupProviderUserOauth = oauths.get(0);
    assertEquals("avans", groupProviderUserOauth.getProvider());
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
  
  @Test
  public void testGetGroupProviderUserOauth() {
    String userId = "urn:collab:person:test.surfguest.nl:tester2";
    GroupProviderUserOauth groupProviderUserOauth = groupProviderServiceSQL.getGroupProviderUserOauth(userId, "avans");
    assertEquals(userId,groupProviderUserOauth.getPersonId());

    groupProviderUserOauth = groupProviderServiceSQL.getGroupProviderUserOauth("does-not-exist", "avans");
    assertTrue(groupProviderUserOauth == null);
}

  @Test
  public void testGetAllGroupProviders() throws Exception {
    List<GroupProvider> all = groupProviderServiceSQL.getAllGroupProviders();
    assertEquals(3,all.size());
    //we need to ensure Grouper is also in here
    boolean grouperPresent = false;
    for (GroupProvider groupProvider : all) {
      if (groupProvider.getGroupProviderType().equals(GroupProviderType.GROUPER)) {
        grouperPresent = true;
        break;
      }
    }
    assertTrue(grouperPresent);
    
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
    GroupProvider groupProvider = providers.get(0);
    List<ServiceProviderGroupAcl> acls = groupProvider.getServiceProviderGroupAcls();
    assertEquals(2, acls.size());
  }

  /* (non-Javadoc)
   * @see nl.surfnet.coin.db.AbstractInMemoryDatabaseTest#getMockDataContentFilename()
   */
  @Override
  public String getMockDataContentFilename() {
    return "test-data-eb.sql";
  }

  /* (non-Javadoc)
   * @see nl.surfnet.coin.db.AbstractInMemoryDatabaseTest#getMockDataCleanUpFilename()
   */
  @Override
  public String getMockDataCleanUpFilename() {
    return "cleanup-test-data-eb.sql";
  }


}
