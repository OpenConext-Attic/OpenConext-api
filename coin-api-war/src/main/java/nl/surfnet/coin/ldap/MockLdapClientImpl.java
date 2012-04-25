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

package nl.surfnet.coin.ldap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import nl.surfnet.coin.api.client.domain.Account;
import nl.surfnet.coin.api.client.domain.Email;
import nl.surfnet.coin.api.client.domain.Name;
import nl.surfnet.coin.api.client.domain.Person;

public class MockLdapClientImpl implements LdapClient {

  @Override
  public Person findPerson(String identifier) {
    Person p = new Person();
    p.setId(identifier);
    p.setName(new Name("the formatted name", "family", "given"));
    p.setDisplayName("his or her display name");
    p.addEmail(new Email("foo@example.com", Email.Type.email));

    final Account account = new Account();
    account.setUserId(identifier);
    account.setUsername("the username");
    p.addAccount(account);
    return p;
  }

  /* (non-Javadoc)
   * @see nl.surfnet.coin.ldap.LdapClient#findPersons(java.util.Collection)
   */
  @Override
  public List<Person> findPersons(Collection<String> identifiers) {
    return Collections.singletonList(findPerson(identifiers.iterator().next()));
  }

}
