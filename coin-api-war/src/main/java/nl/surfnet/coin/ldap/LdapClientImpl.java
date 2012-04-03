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

import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

import nl.surfnet.coin.api.client.domain.Person;

/**
 * Interface to Ldap where all persons are stored.
 * 
 */
public class LdapClientImpl implements LdapClient{
  
  @Autowired
  private LdapTemplate ldapTemplate;

  /**
   * 
   * Find the Person in the LDAP. The identifier can either be the urn
   * (urn:collab:person:nl.myuniversity:s123456) or the persistent identifier (hashed urn specific for the SP).
   * 
   * @param identifier
   *          unqiue identifier of the Person
   * @return Person object
   */
  @Override
  @SuppressWarnings("unchecked")
  public Person findPerson(String identifier) {
    List<Person> search;
    search = (List<Person>) ldapTemplate.search(
        "", "(objectclass=collabPerson)",
        new AttributesMapper() {
          
          @Override
          public Person mapFromAttributes(Attributes attributes) throws NamingException {
            Person person = new Person();
           // person.setNickname((String)attributes.get("cn").get());
            return person;
          }
        });
    
    return search.get(0);
  }

  /**
   * @param ldapTemplate the ldapTemplate to set
   */
  public void setLdapTemplate(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
  }

}
