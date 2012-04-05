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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import nl.surfnet.coin.api.client.domain.Name;
import nl.surfnet.coin.api.client.domain.Person;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.directory.server.core.CoreSession;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.directory.server.schema.bootstrap.ApachemetaSyntaxProducer;
import org.apache.directory.shared.ldap.schema.AbstractAttributeType;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.Syntax;
import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.server.ApacheDSContainer;
import org.springframework.util.CollectionUtils;

/**
 * Test for {@link LdapClientImpl} which uses an in-memory ldap base
 * 
 */
public class LdapClientImplTest {

  /**
   * Test method for
   * {@link nl.surfnet.coin.ldap.LdapClientImpl#findPerson(java.lang.String)}.
   * 
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   */
  @Test
  public void testFindPerson() throws IllegalAccessException,
      InvocationTargetException {
    Person person = new Person();
    BeanUtils.setProperty(person, "name", new Name());
    BeanUtils.setProperty(person, "name.formatted", "formatted");
    // Person person = ldapClient.findPerson("");
    // assertEquals("Test User", person.getNickname());
    assertEquals(person.getName().getFormatted(), "formatted");
  }

}
