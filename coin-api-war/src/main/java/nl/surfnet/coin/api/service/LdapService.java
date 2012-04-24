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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.ldap.LdapClient;

@Component(value = "ldapService")
public class LdapService implements PersonService {

  @Autowired
  private LdapClient ldapClient;

  @Override
  public PersonEntry getPerson(String userId, String onBehalfOf) {
    return new PersonEntry(ldapClient.findPerson(userId), 1, 0, null, 1);
  }

  @Override
  public GroupMembersEntry getGroupMembers(String groupId, String onBehalfOf) {
    return null;
  }
}
