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

import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 
 *
 */
public class OpenConextOAuthClientImplTest {
  private static final Logger LOG = LoggerFactory.getLogger(OpenConextOAuthClientImplTest.class);

  private static final String USER_ID = "urn:collab:person:test.surfguest.nl:mnice";
  private static final String GROUP_ID = "urn:collab:group:test.surfteams.nl:nl:surfnet:diensten:managementvo";
  private OpenConextOAuthClientImpl client;
  private ResourceRequestHandler requestHandler;
  private LocalTestServer testServer;

  public static class ResourceRequestHandler implements HttpRequestHandler {

    private Resource resource;
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
      LOG.debug("Getting request: {}", request.getRequestLine());
      response.setEntity(new InputStreamEntity(resource.getInputStream(), resource.contentLength()));

    }

    public void setResource(Resource resource) {
      this.resource = resource;
    }
  }

  @Before
  public void initialize() throws Exception {

    requestHandler = new ResourceRequestHandler();

    testServer = new LocalTestServer(null, null);
    testServer.register("/whatever/*", requestHandler);
//    testServer.g
    testServer.start();
    client = new OpenConextOAuthClientImpl();
    client.setEndpointBaseUrl("http://" + testServer.getServiceAddress().getHostString() + ":" + testServer.getServiceAddress().getPort() + "/whatever/");
    client.setConsumerKey("key");
    client.setConsumerSecret("secret");
    OAuthRepository repository = new InMemoryOAuthRepositoryImpl();
    repository.storeToken("key", USER_ID);
    repository.storeToken("key", null); // for client creds
    client.setRepository(repository);


  }

  @After
  public void cleanup() throws Exception {
    testServer.stop();
  }
  /**
   * Test method for
   * {@link nl.surfnet.coin.api.client.OpenConextOAuthClientImpl#getPerson(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testGetPerson() {
    requestHandler.setResource(new ClassPathResource("single-person.json"));
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
    requestHandler.setResource(new ClassPathResource("single-person-server-output.json"));
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
    requestHandler.setResource(new ClassPathResource("multiple-persons.json"));
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
    requestHandler.setResource(new ClassPathResource("multiple-groups20.json"));
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
    requestHandler.setResource(new ClassPathResource("multiple-groups.json"));
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
    requestHandler.setResource(new ClassPathResource("multiple-wrapped-teammembers.json"));
     List<Person> groupMembers = this.client.getGroupMembers(GROUP_ID, USER_ID);
    assertEquals(12, groupMembers.size());
  }

  
}
