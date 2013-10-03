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

import java.util.*;

import nl.surfnet.coin.api.client.domain.Email;
import nl.surfnet.coin.api.client.domain.Name;
import nl.surfnet.coin.api.client.domain.Organization;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.janus.domain.ARP;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class PersonARPEnforcerTest {

  PersonARPEnforcer e = new PersonARPEnforcer();

  @Test
  public void allowAll() throws Exception {
  // with a 'null' arp, no restrictions
    Person p = new Person();
    ARP arp = null;
    Person enforced = e.enforceARP(p, arp);
    assertThat(enforced, equalTo(p));
  }

  @Test
  public void allowNone() {
    Person p = initPerson();
    Set<Organization> organizations = new HashSet<Organization>();
    Organization org = new Organization("topSecret");
    organizations.add(org);
    p.setOrganizations(organizations);
    ARP arp = new ARP();
    Map<String, List<Object>> attr = new HashMap<String, List<Object>>();
    arp.setAttributes(attr);
    Person enforced = e.enforceARP(p, arp);
    assertNull(enforced.getDisplayName());
    assertNull(enforced.getName());
    
  }

  private Person initPerson() {
    Person p = new Person();
    p.setDisplayName("boobaa");
    p.setName(new Name("boo", "baa", "bii"));
    return p;
  }

  @Test
  public void nameOnly() {
    // ARP contains one of the name attributes. Enforced person should contain all name attributes, but no email
    Person p = initPerson();
    p.setEmails(Collections.singleton(new Email("foo@bar.com")));
    ARP arp = new ARP();
    arp.getAttributes().put("urn:mace:dir:attribute-def:displayName", Collections.<Object>singletonList("*"));
    Person enforced = e.enforceARP(p, arp);
    assertThat(enforced.getDisplayName(), equalTo("boobaa"));
    assertThat(enforced.getName().getFamilyName(), equalTo("baa"));
    assertThat(enforced.getEmails(), nullValue());
  }

  @Test
  public void id() {
    // ARP contains one of the id attributes. Enforced person should contain id attributes
    Person p = initPerson();
    p.setId("myid");
    ARP arp = new ARP();
    arp.getAttributes().put("urn:oid:1.3.6.1.4.1.1076.20.40.40.1", Collections.<Object>singletonList("*"));
    Person enforced = e.enforceARP(p, arp);
    assertThat(enforced.getId(), equalTo("myid"));
    assertThat(enforced.getDisplayName(), nullValue());
  }

  @Test
  public void testArpWithPrefixMatchingInformation() {
    Person p = initPerson();
    ARP arp = new ARP();
    arp.getAttributes().put(PersonARPEnforcer.Attribute.UID.name, Arrays.<Object>asList("*", true));
    Person enforced = e.enforceARP(p, arp);
    assertNull(enforced.getDisplayName());

  }

  @Test
  public void testNoArp() {
    Person p = new Person();
    p.setDisplayName("john.doe");
    ARP arp = new ARP();
    arp.setNoArp(true);

    Person enforced = e.enforceARP(p, arp);
    assertEquals(p.getDisplayName(), enforced.getDisplayName());
  }
}
