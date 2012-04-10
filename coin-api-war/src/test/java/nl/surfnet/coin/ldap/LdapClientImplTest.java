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

import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.eb.EngineBlockImpl;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapOperations;

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
  public void testFindPerson() throws IllegalAccessException, InvocationTargetException {
    LdapOperations ldapOperations = Mockito.mock(LdapOperations.class);
    LdapClientImpl ldapClient = new LdapClientImpl();
    ldapClient.setEngineBlock(new EngineBlockImpl());
    ldapClient.setLdapOperations(ldapOperations);

    Mockito.when(ldapOperations.search(Mockito.anyString(), Mockito.anyString(), (AttributesMapper) Mockito.any()))
        .thenReturn(Collections.EMPTY_LIST);

    Person findPerson = ldapClient.findPerson("urn:collab:person:myuniversity:john.doe");
    assertNull(findPerson);
  }

}
