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

import java.util.Collections;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.Test;

import nl.surfnet.coin.api.client.domain.Email;
import nl.surfnet.coin.api.client.domain.Name;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.janus.domain.ARP;

import static org.junit.Assert.assertThat;

public class PersonARPEnforcerTest {

  PersonARPEnforcer e = new PersonARPEnforcer();
  @Test
  public void allowAll() throws Exception {
  // with a 'null' arp, no restrictions
    Person p = new Person();
    ARP arp = null;
    Person enforced = e.enforceARP(p, arp);
    assertThat(enforced, IsEqual.equalTo(p));
  }

  @Test
  public void allowNone() {
    Person p = new Person();
    p.setDisplayName("boobaa");
    p.setName(new Name("boo", "baa", "bii"));
    ARP arp = new ARP();
    Person enforced = e.enforceARP(p, arp);
    assertThat(enforced.getDisplayName(), IsNull.nullValue());
    assertThat(enforced.getName(), IsNull.nullValue());
  }

  @Test
  public void nameOnly() {
    // ARP contains one of the name attributes. Enforced person should contain all name attributes, but no email
    Person p = new Person();
    p.setDisplayName("boobaa");
    p.setName(new Name("boo", "baa", "bii"));
    p.setEmails(Collections.singleton(new Email("foo@bar.com")));
    ARP arp = new ARP();
    arp.getAttributes().put("urn:mace:dir:attribute-def:displayName", Collections.<Object>singletonList("*"));
    Person enforced = e.enforceARP(p, arp);
    assertThat(enforced.getDisplayName(), IsEqual.equalTo("boobaa"));
    assertThat(enforced.getName().getFamilyName(), IsEqual.equalTo("baa"));
    assertThat(enforced.getEmails(), IsNull.nullValue());
  }
}
