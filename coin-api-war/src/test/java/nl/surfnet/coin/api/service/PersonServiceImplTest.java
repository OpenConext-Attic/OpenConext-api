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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class PersonServiceImplTest {


  @InjectMocks
  PersonServiceImpl svc;

  @Mock
  PersonARPEnforcer arpEnforcer;

  @Mock
  ApiGrouperDao apiGrouperDao;

  @Mock
  LdapClient ldapClient;

  @Mock
  OpenConextClientDetailsService clientDetailsService;

  @Before
  public void setup() {
    svc = new PersonServiceImpl();
    MockitoAnnotations.initMocks(this);
  }
  @Test
  public void enforceArpOnGetGroupMembers() throws Exception {

    Person originalPerson = new Person();
    originalPerson.setDisplayName("originalDisplayName");
    originalPerson.setId("theid");

    when(apiGrouperDao.findAllMembers(eq("groupId"), anyInt(), anyInt())).thenReturn(new GroupMembersEntry(Arrays.asList(originalPerson)));

    when(ldapClient.findPersons(Arrays.asList("theid"))).thenReturn(Arrays.asList(originalPerson));

    Person arpedPerson = new Person();
    arpedPerson.setDisplayName("the arped display name");

    when(arpEnforcer.enforceARP((Person) any(), (ARP) any())).thenReturn(arpedPerson);

    GroupMembersEntry groupMembers = svc.getGroupMembers("groupId", "onbehalfof", "spentityid", 2, 0, null);

    assertEquals("the arped display name", groupMembers.getEntry().get(0).getDisplayName());
  }
}
