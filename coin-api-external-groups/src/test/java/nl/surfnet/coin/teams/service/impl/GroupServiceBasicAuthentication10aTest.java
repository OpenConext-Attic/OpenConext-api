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

import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.PASSWORD;
import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.URL;
import static nl.surfnet.coin.teams.util.GroupProviderOptionParameters.USERNAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.mock.AbstractMockHttpServerTest;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderType;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

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

  @Test
  public void testGetGroupFaultyJsonResponse() {
    super.setResponseResource(new ByteArrayResource("not-even-valid-json".getBytes()));
    Group20 group20 = groupService.getGroup20(provider,"personId", "whatever");
    assertNull(group20);
  }

  @Test
  public void testGetGroupFaultyHttpResponse() {
    super.setResponseResource(new ByteArrayResource("not-even-valid-json".getBytes()));
    super.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    Group20 group20 = groupService.getGroup20(provider,"personId", "whatever");
    assertNull(group20);
  }

  @Test
  public void testGetGroupMembersEntryFaultyJsonResponse() {
    super.setResponseResource(new ByteArrayResource("not-even-valid-json".getBytes()));
    GroupMembersEntry groupMembersEntry = groupService.getGroupMembersEntry(provider,"personId", "whatever", 0, 0);
    assertTrue(groupMembersEntry.getEntry().isEmpty());
  }

  @Test
  public void testGetGroupMembersEntryFaultyHttpResponse() {
    super.setResponseResource(new ByteArrayResource("not-even-valid-json".getBytes()));
    super.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    GroupMembersEntry groupMembersEntry = groupService.getGroupMembersEntry(provider,"personId", "whatever", 0, 0);
    assertTrue(groupMembersEntry.getEntry().isEmpty());
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
