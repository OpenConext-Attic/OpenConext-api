/*
 * Copyright 2013 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.coin.api.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.janus.domain.ARP;
import nl.surfnet.coin.ldap.LdapClient;
import nl.surfnet.coin.teams.service.impl.ApiGrouperDao;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class PersonServiceImplTest {

  @Mock
  private ApiGrouperDao apiGrouperDao;

  @Mock
  LdapClient ldapClient;

  @InjectMocks
  PersonServiceImpl personService;

  @Mock
  OpenConextClientDetailsService clientDetailsService;

  @Before
  public void before() {
    personService = new PersonServiceImpl();
    MockitoAnnotations.initMocks(this);
  }
  @Test
  public void testGetGroupMembers() throws Exception {

    Person person = new Person();
    person.setId("thePersonId");
    person.setDisplayName("the display name");
    when(apiGrouperDao.findAllMembers(eq("groupId"), anyInt(), anyInt())).thenReturn(new GroupMembersEntry(Arrays.asList(person)));
    when(ldapClient.findPersons(Arrays.asList("thePersonId"))).thenReturn(Arrays.asList(person));
    ARP arp = new ARP();
    arp.setAttributes(new HashMap<String, List<Object>>());
    arp.getAttributes().put(PersonARPEnforcer.Attribute.COLLABPERSONID.name, null);
    when(clientDetailsService.getArp("spEntityId")).thenReturn(arp);


    GroupMembersEntry groupMembers = personService.getGroupMembers("groupId", "onBehalfOf", "spEntityId", 10, 0, "");
    Person person1 = groupMembers.getEntry().get(0);
    assertEquals("Id is allowed by ARP, should be same", "thePersonId", person1.getId());
    assertEquals("DisplayName is not allowed by ARP, should be null", null, person1.getDisplayName());

  }
}
