/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nl.surfnet.coin.ldap;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import nl.surfnet.coin.api.client.domain.Account;
import nl.surfnet.coin.api.client.domain.Email;
import nl.surfnet.coin.api.client.domain.Name;
import nl.surfnet.coin.api.client.domain.Organization;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.eb.EngineBlock;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Interface to Ldap where all persons are stored.
 * 
 */
public class LdapClientImpl implements LdapClient {

  @Autowired
  private LdapOperations ldapOperations;

  @Autowired
  private EngineBlock engineBlock;

  /**
   * 
   * Find the Person in the LDAP. The identifier can either be the urn
   * (urn:collab:person:nl.myuniversity:s123456) or the persistent identifier
   * (hashed urn specific for the SP).
   * 
   * @param identifier
   *          unqiue identifier of the Person
   * @return Person object
   */
  @Override
  @SuppressWarnings("unchecked")
  public Person findPerson(String identifier) {
    identifier = engineBlock.getPersistentNameIdentifier(identifier);
    AndFilter filter = new AndFilter().and(new EqualsFilter("objectclass", "collabPerson")).and(
        new EqualsFilter("collabpersonid", identifier));
    String encode = filter.encode();
    List<Person> search = (List<Person>) ldapOperations.search("", encode, new AttributesMapper() {
      @Override
      public Person mapFromAttributes(Attributes attributes) throws NamingException {
        return convertLdapProperties(new Person(), attributes);
      }
    });
    if (CollectionUtils.isEmpty(search)) {
      return null;
    }
    if (search.size() > 1)
      throw new RuntimeException("Found more then one LDAP entry for identifier(" + identifier + ")");
    return search.get(0);
  }

  /*
   * "collabpersonid" => "id" , 'collabpersonisguest' => 'person.tags', 'uid' =>
   * array( 'account.username', 'account.userId', ), "givenname" =>
   * "name.givenName", 'sn' => 'name.familyName', 'cn' => 'name.formatted',
   * "displayname" => array( "displayName", "nickname" ) , "mail" => "emails",
   * 'o' => 'organizations.name', 'schachomeorganizationtype' =>
   * 'organizations.type', 'nledupersonorgunit' => 'organizations.department',
   * 'edupersonaffiliation' => 'organizations.title',
   */
  private Person convertLdapProperties(Person person, Attributes attributes) {
    person.setId(getAttribute("collabpersonid", attributes));
    person.addTag(Boolean.valueOf(getAttribute("collabpersonisguest", attributes)) ? "member" : "guest");
    String uid = getAttribute("uid", attributes);
    if (StringUtils.hasText(uid)) {
      person.addAccount(new Account(uid, uid));
    }
    person.setName(new Name(getAttribute("cn", attributes), getAttribute("sn", attributes), getAttribute("givenname",
        attributes)));
    person.setDisplayName(getAttribute("displayname", attributes));
    person.setNickname(getAttribute("displayname", attributes));
    String mail = getAttribute("mail", attributes);
    if (StringUtils.hasText(mail)) {
      person.addEmail(new Email(mail));
    }
    person.addOrganization(new Organization(getAttribute("o", attributes), getAttribute("schachomeorganizationtype",
        attributes), getAttribute("nledupersonorgunit", attributes), getAttribute("edupersonaffiliation", attributes)));
    return person;
  }

  private String getAttribute(String attrID, Attributes attributes) {
    Attribute attribute = attributes.get(attrID);
    try {
      return attribute != null ? (String) attribute.get() : null;
    } catch (NamingException e) {
      // ignore this as we can't recover
      return null;
    }
  }

  /**
   * @param ldapOperations
   *          the ldapOperations to set
   */
  public void setLdapOperations(LdapOperations ldapOperations) {
    this.ldapOperations = ldapOperations;
  }

  /**
   * @param engineBlock
   *          the engineBlock to set
   */
  public void setEngineBlock(EngineBlock engineBlock) {
    this.engineBlock = engineBlock;
  }

}
