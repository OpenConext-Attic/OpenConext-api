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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.mock.AbstractMockHttpServerTest;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderType;
import nl.surfnet.coin.teams.domain.GroupProviderUserOauth;

import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.*;
import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.CONSUMER_SECRET;
import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.URL;
import static org.junit.Assert.assertEquals;

/**
 * More then junit test, but does not depend on any external resources
 * 
 */
public class GroupServiceBasicAuthentication10aTest extends
    AbstractMockHttpServerTest {

  private GroupServiceBasicAuthentication groupService;
  private GroupProvider provider;

  @Test
  public void testGetGroupMembersAvans() {
    super.setResponseResource(new ClassPathResource("avans-teammembers.json"));
    GroupMembersEntry entry = groupService.getGroupMembersEntry(provider, "personId", "urn:collab:group:avans.nl:testgroup",2147483647,0);
    assertEquals(12, entry.getEntry().size());
  }

  @Test
  public void testGetGroup20EntryHz() {
    super.setResponseResource(new ClassPathResource("hz-groups.json"));
    final Group20Entry group20Entry = groupService.getGroup20Entry(provider,"person", 2147483647, 0);
    final List<Group20> group20s = group20Entry.getEntry();
    assertEquals(3, group20Entry.getTotalResults());
    assertEquals(3, group20s.size());
    assertEquals("HZG-1042", group20s.get(0).getId());
  }

  @Test
  public void testGetGroup20Hz() {
    super.setResponseResource(new ClassPathResource("hz-group.json"));
    Group20 group20 = groupService.getGroup20(provider,"personId", "HZG-1042");
    assertEquals("HZG-1042", group20.getId());
  }

  @Before
  public void before() {
    groupService = new GroupServiceBasicAuthentication();
    provider = new GroupProvider(1L, "provider", "provider",
        GroupProviderType.BASIC_AUTHENTICATION.toString());
    provider.addAllowedOption(URL, "http://localhost:8088/social");
    provider.addAllowedOption(USERNAME, "user_name");
    provider.addAllowedOption(PASSWORD, "password");
  }
}
