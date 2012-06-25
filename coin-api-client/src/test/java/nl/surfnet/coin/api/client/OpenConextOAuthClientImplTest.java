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

package nl.surfnet.coin.api.client;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.scribe.model.Token;
import org.springframework.core.io.ClassPathResource;

import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.mock.AbstractMockHttpServerTest;

import static org.junit.Assert.assertEquals;

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
    client = new OpenConextOAuthClientImpl();
    client.setEndpointBaseUrl("http://localhost:8088/whatever");
    client.setConsumerKey("key");
    client.setConsumerSecret("secret");
    OAuthRepository repository = new InMemoryOAuthRepositoryImpl();
    repository.storeToken(new Token("key", "secret"), USER_ID, OAuthVersion.v10a);
    client.setRepository(repository);
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
   * {@link nl.surfnet.coin.api.client.OpenConextOAuthClientImpl#getPerson(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetPersonServerOutput() {
    super.setResponseResource(new ClassPathResource("single-person-server-output.json"));
    Person person = this.client.getPerson(USER_ID, null);
    assertEquals("mFoo@surfguest.nl", person.getEmails().iterator().next()
        .getValue());
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.api.client.OpenConextOAuthClientImpl#getGroupMembers(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetGroupMembers() {
    super.setResponseResource(new ClassPathResource("multiple-persons.json"));
    List<Person> persons = this.client.getGroupMembers(GROUP_ID, USER_ID);
    assertEquals(22, persons.size());
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.api.client.OpenConextOAuthClientImpl#getGroups20(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetGroups20() {
    super.setResponseResource(new ClassPathResource("multiple-groups20.json"));
    List<Group20> groups20 = this.client.getGroups20(USER_ID, USER_ID);
    assertEquals(3, groups20.size());
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

  /**
   * Test method for
   * {@link nl.surfnet.coin.api.client.OpenConextOAuthClientImpl#getGroups(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetWrappedGroupMembers() {
    super.setResponseResource(new ClassPathResource("multiple-wrapped-teammembers.json"));
     List<Person> groupMembers = this.client.getGroupMembers(GROUP_ID, USER_ID);
    assertEquals(12, groupMembers.size());
  }

  
}
