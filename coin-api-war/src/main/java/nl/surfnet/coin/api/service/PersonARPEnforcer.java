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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.janus.domain.ARP;

/**
 * Helper class to enforce a given ARP onto a given Person object.
 *
 */
public class PersonARPEnforcer {

  public static enum Attribute {
    UID("urn:mace:dir:attribute-def:uid"),
    CN("urn:mace:dir:attribute-def:cn"),
    SN("urn:mace:dir:attribute-def:sn"),
    DISPLAY_NAME("urn:mace:dir:attribute-def:displayName"),
    GIVEN_NAME("urn:mace:dir:attribute-def:givenName"),
    EDU_NICK("urn:mace:dir:attribute-def:eduPersonNickname"),
    EDU_PRINCIPAL("urn:mace:dir:attribute-def:eduPersonPrincipalName"),

    EDU_ORGUNITDN("urn:mace:dir:attribute-def:eduPersonOrgUnitDN"),
    NL_ORGUNITDN("urn:mace:surffederatie.nl:attribute-def:nlEduPersonOrgUni"),
    SCHAC_HOMEORG("urn:mace:terena.org:attribute-def:schacHomeOrganization"),
    EMAIL("urn:mace:dir:attribute-def:mail"),
    COLLABPERSONISGUEST("collabpersonisguest")
    ;


    public String name;

    Attribute(String attrName) {
      this.name = attrName;
    }
  }

  private final static List<String> nameAttributes = Arrays.asList(
      Attribute.CN.name,
      Attribute.GIVEN_NAME.name,
      Attribute.DISPLAY_NAME.name,
      Attribute.SN.name,
      Attribute.EDU_NICK.name,
      Attribute.EDU_PRINCIPAL.name
  );
  private final static List<String> organizationAttributes= Arrays.asList(
      Attribute.NL_ORGUNITDN.name,
      Attribute.EDU_ORGUNITDN.name,
      Attribute.SCHAC_HOMEORG.name
  );


  /**
   * Mangle a given Person using the given ARP.
   *
   * @param person Person to mangle
   * @param arp    ARP to use
   * @return A copy of the given person, mangled
   */
  public Person enforceARP(Person person, ARP arp) {
    Assert.notNull(person);

    // No arp at all: allow everything
    if (arp == null) {
      return person;
    }

    Person newP = new Person();
    if (arp.getAttributes() == null || arp.getAttributes().isEmpty()) {
      // Empty arp: allow nothing
      return newP;
    }

    // Name attributes: allow all in case any name attribute is allowed (simplicity/usability sake)
    final Set<String> arpAttributeNames = arp.getAttributes().keySet();
    if (CollectionUtils.containsAny(nameAttributes, arpAttributeNames)) {
      newP.setDisplayName(person.getDisplayName());
      newP.setName(person.getName());
      newP.setNickname(person.getNickname());
    }

    // organization attributes: allow all in case any name attribute is allowed (simplicity/usability sake)
    if (CollectionUtils.containsAny(organizationAttributes, arpAttributeNames)) {
      newP.setOrganizations(person.getOrganizations());
    }

    // Email
    if (arpAttributeNames.contains(Attribute.EMAIL.name)) {
      newP.setEmails(person.getEmails());
    }

    if (arpAttributeNames.contains(Attribute.UID.name)) {
      newP.setAccounts(person.getAccounts());
    }

    if (arpAttributeNames.contains(Attribute.UID.name)) {
      newP.setTags(person.getTags());
    }

    return newP;
  }
}
