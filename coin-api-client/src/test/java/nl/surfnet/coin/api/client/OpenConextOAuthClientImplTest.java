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
package nl.surfnet.coin.api.client;

import static org.junit.Assert.assertEquals;

import java.util.List;

import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.mock.AbstractMockHttpServerTest;

import org.junit.Before;
import org.junit.Test;
import org.scribe.model.Token;
import org.springframework.core.io.ClassPathResource;

/**
 * 
 *
 */
public class OpenConextOAuthClientImplTest extends AbstractMockHttpServerTest {

  private static final String USER_ID = "urn:collab:person:test.surfguest.nl:mnice";
  private static final String GROUP_ID = "urn:collab:group:test.surfteams.nl:nl:surfnet:diensten:managementvo";
  private OpenConextOAuthClientImpl client;

  @Before
  public void initialize() throws Exception {
    OAuthEnvironment environment = new OAuthEnvironment();
    environment.setEndpointBaseUrl("http://localhost:8088/whatever");
    environment.setOauthKey("key");
    environment.setOauthSecret("secret");
    environment.setCallbackUrl("http://notneededyet");
    OAuthRepository repository = new InMemoryOAuthRepositoryImpl();
    repository.storeToken(new Token("token", "secret"), USER_ID,
        OAuthVersion.v2);
    client = new OpenConextOAuthClientImpl(environment, repository);
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.api.client.OpenConextOAuthClientImpl#getPerson(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetPerson() {
    super.setResponseResource(new ClassPathResource("single-person.json"));
    Person person = this.client.getPerson(USER_ID, USER_ID);
    assertEquals("mnice@surfguest.nl", person.getEmails().iterator().next()
        .getValue());
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.api.client.OpenConextOAuthClientImpl#getPeople(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetPeople() {
    super.setResponseResource(new ClassPathResource("multiple-persons.json"));
    List<Person> persons = this.client.getGroupMembers(GROUP_ID, USER_ID);
    assertEquals(22, persons.size());
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.api.client.OpenConextOAuthClientImpl#getGroups(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetGroups() {
    super.setResponseResource(new ClassPathResource("multiple-groups.json"));
    List<Group> groups = this.client.getGroups(USER_ID, USER_ID);
    assertEquals(17, groups.size());
  }

}
