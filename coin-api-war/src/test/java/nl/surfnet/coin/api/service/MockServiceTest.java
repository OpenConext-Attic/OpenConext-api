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

import org.junit.Test;

import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;

import static org.junit.Assert.assertEquals;

public class MockServiceTest {

  private MockServiceImpl service = new MockServiceImpl();

  
  
  @Test
  public void getPerson() {
    Person person = service.getPerson("foo", "some logged in user").getEntry();
    assertEquals("Foo", person.getName().getFamilyName());
  }

  @Test
  public void getPersonSpecialChars() {
    Person person = service.getPerson("spec-ial:cha_rs*in^file.name", "some logged in user").getEntry();
    assertEquals("myspecialcharsfamilyname", person.getName().getFamilyName());
  }

  @Test
  public void getGroupMembers() {
    List<Person> groupMembers = service.getGroupMembers("foo", "some logged in user", 1, 0, "").getEntry();
    assertEquals(3, groupMembers.size());
  }

  @Test
  public void getGroups() {
    List<Group> groups = service.getGroups("foo", "some logged in user", 10, 0, null).getEntry();
    assertEquals(2, groups.size());
  }

  @Test
  public void getGroups20() {
    List<Group20> groups = service.getGroups20("foo", "some logged in user", 10, 0, null).getEntry();
    assertEquals(2, groups.size());
  }

  @Test
  public void getPersonFallback() {
    Person person = service.getPerson("qwerty", "some logged in user").getEntry();
    assertEquals("Nice", person.getName().getFamilyName());
  }

  @Test
  public void getGroupMembersFallback() {
    List<Person> groupMembers = service.getGroupMembers("qwerty", "some logged in user", 1, 0, "").getEntry();
    assertEquals(22, groupMembers.size());
  }

  @Test
  public void getGroupsFallback() {
    List<Group> groups = service.getGroups("qwerty", "some logged in user", 10, 0, null).getEntry();
    assertEquals(17, groups.size());
  }

  @Test
  public void getGroups20Fallback() {
    List<Group20> groups = service.getGroups20("qwerty", "some logged in user", 10, 0, null).getEntry();
    assertEquals(2, groups.size());
  }

  @Test
  public void testInjection() {
    service.setActive(true);
    Group20 group = new Group20();
    group.setDescription("description");
    group.setId("group1");
    service.addGroup(group);
    Person person = new Person();
    person.setId("person1");
    service.addPerson(person);
    service.addGroup(group);
    service.addPersonToGroup(person.getId(), group.getId());
    GroupMembersEntry groupMembers = service.getGroupMembers(group.getId(), null, 1, 0, "");
    assertEquals(1, groupMembers.getEntry().size());
    service.setActive(false);
  }

}
