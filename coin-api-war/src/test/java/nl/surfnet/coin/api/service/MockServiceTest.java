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

package nl.surfnet.coin.api.service;

import java.util.List;

import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Person;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MockServiceTest {

  private MockService service = new MockService();

  @Test
  public void getPerson() {
    Person person = service.getPerson("foo", "some logged in user");
    assertEquals("Foo", person.getName().getFamilyName());
  }

  @Test
  public void getGroupMembers() {
    List<Person> groupMembers = service.getGroupMembers("foo",
        "some logged in user");
    assertEquals(3, groupMembers.size());
  }

  @Test
  public void getGroups() {
    List<Group> groups = service.getGroups("foo", "some logged in user");
    assertEquals(2, groups.size());
  }

  @Test
  public void getPersonFallback() {
    Person person = service.getPerson("qwerty", "some logged in user");
    assertEquals("Nice", person.getName().getFamilyName());
  }

  @Test
  public void getGroupMembersFallback() {
    List<Person> groupMembers = service.getGroupMembers("qwerty",
        "some logged in user");
    assertEquals(22, groupMembers.size());
  }

  @Test
  public void getGroupsFallback() {
    List<Group> groups = service.getGroups("qwerty", "some logged in user");
    assertEquals(17, groups.size());
  }

}
