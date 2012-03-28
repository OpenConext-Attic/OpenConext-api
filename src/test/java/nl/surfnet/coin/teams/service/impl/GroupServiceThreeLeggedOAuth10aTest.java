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

import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.CONSUMER_KEY;
import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.CONSUMER_SECRET;
import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.URL;
import static org.junit.Assert.*;

import java.util.List;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.mock.AbstractMockHttpServerTest;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderType;
import nl.surfnet.coin.teams.domain.GroupProviderUserOauth;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * More then junit test, but does not depend on any external resources
 * 
 */
public class GroupServiceThreeLeggedOAuth10aTest extends
    AbstractMockHttpServerTest {

  private GroupServiceThreeLeggedOAuth10a groupService;
  private GroupProviderUserOauth oauth = new GroupProviderUserOauth("person",
      "provider", "oAuthToken", "oAuthSecret");
  private GroupProvider provider;

  /**
   * Test method for
   * {@link nl.surfnet.coin.teams.service.impl.GroupServiceThreeLeggedOAuth10a#getGroup20s(nl.surfnet.coin.teams.domain.GroupProviderUserOauth)}
   * .
   */
  @Test
  public void testGetGroup20sAvans() {
    super.setResponseResource(new ClassPathResource("avans-groups.json"));
    List<Group20> group20s = groupService.getGroup20s(oauth, provider);
    assertEquals(1, group20s.size());
    assertEquals("nl.avans.AVANS-employee_grp", group20s.get(0).getId());
  }
  
  /**
   * Test method for
   * {@link nl.surfnet.coin.teams.service.impl.GroupServiceThreeLeggedOAuth10a#getGroup20s(nl.surfnet.coin.teams.domain.GroupProviderUserOauth)}
   * .
   */
  @Test
  public void testGetGroup20sHz() {
    super.setResponseResource(new ClassPathResource("hz-groups.json"));
    List<Group20> group20s = groupService.getGroup20s(oauth, provider);
    assertEquals(3, group20s.size());
    assertEquals("HZG-1042", group20s.get(0).getId());
  }

  @Before
  public void before() {
    groupService = new GroupServiceThreeLeggedOAuth10a();
    provider = new GroupProvider(1L, "provider", "provider",
        GroupProviderType.OAUTH_THREELEGGED.toString());
    provider.addAllowedOption(URL, "http://localhost:8088/social");
    provider.addAllowedOption(CONSUMER_KEY, "consumer_key");
    provider.addAllowedOption(CONSUMER_SECRET, "consumer_decret");
  }
}
