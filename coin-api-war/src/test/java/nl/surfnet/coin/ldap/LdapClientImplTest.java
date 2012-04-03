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

import nl.surfnet.coin.api.client.domain.Person;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.server.ApacheDSContainer;
import org.springframework.util.CollectionUtils;

/**
 * Test for {@link LdapClientImpl} which uses an in-memory ldap base
 * 
 */
public class LdapClientImplTest {

  private static LdapClientImpl ldapClient;
  private static ApacheDSContainer ldapServer;

  @BeforeClass
  public static void startEmbeddedLdap() throws Exception {
    deleteWorkingDirectory();

    LdapClientImplTest.ldapServer = new ApacheDSContainer(
        "dc=surfconext,dc=nl", "classpath:ldif/person.ldif");
    ldapServer.setPort(33389);
    ldapServer.afterPropertiesSet();
    LdapContextSource context = new LdapContextSource();
    context.setUrl("ldap://localhost:33389");
    context.setBase("dc=surfconext,dc=nl");
    context.setUserDn("cn=engine,dc=surfconext,dc=nl");
    context.setPassword("secret");
    context.setAnonymousReadOnly(true);
    context.afterPropertiesSet();

    LdapTemplate ldapTemplate = new LdapTemplate(context);
    LdapClientImplTest.ldapClient = new LdapClientImpl();
    ldapClient.setLdapTemplate(ldapTemplate);
  }

  private static void deleteWorkingDirectory() {
    String loc = System.getProperty("java.io.tmpdir")
        + "apacheds-spring-security";
    File file = new File(loc);
    if (file.isDirectory()) {
      String[] children = file.list();
      if (children.length < 1) {
        file.delete();
      } else {
        for (String child : children) {
          deleteDir(new File(file, child));
        }
      }
    }
  }

  private static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (String child : children) {
        boolean success = deleteDir(new File(dir, child));
        if (!success) {
          return false;
        }
      }
    }

    return dir.delete();
  }

  @AfterClass
  public static void stopEmbeddedLdap() throws Exception {
    ldapServer.destroy();
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.ldap.LdapClientImpl#findPerson(java.lang.String)}.
   */
  @Test
  public void testFindPerson() {
    Person person = ldapClient.findPerson("");
  }

}
