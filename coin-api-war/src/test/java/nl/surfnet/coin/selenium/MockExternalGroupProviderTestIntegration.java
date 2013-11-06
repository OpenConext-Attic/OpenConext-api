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

package nl.surfnet.coin.selenium;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import nl.surfnet.coin.api.client.domain.Email;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.internal.OpenConextApi10aTwoLegged;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

public class MockExternalGroupProviderTestIntegration {

  private final static ObjectMapper objectMapper = new ObjectMapper()
      .enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

  private OAuthService service;
  private Token token;

  private static final String OAUTH_KEY = "key";
  private static final String OAUTH_SECRET = "mysecret";
  private static final String OS_URL = "mock10/social/rest/";
  private static final String BASIC_URL = "mockbasic/social/rest/";

  @Before
  public void before() {
    /*
     * We choose to use two-legged oauth for testing the Mock Provider because
     * the setup is much simpler. In the actual selenium tests (this is an
     * integration test), we test the three-legged variant but not the underlying
     * inject functionality
     */
    service = new ServiceBuilder().provider(new OpenConextApi10aTwoLegged()).apiKey(OAUTH_KEY).apiSecret(OAUTH_SECRET)
        .debug().build();
    token = new Token("", "");
    reset();
  }

  @After
  public void cleanup() {
    // Reset the mock service, for potential other tests against the same running container
    reset();
  }

  @Test
  public void testUserInjectionPerson() {
    testAddSearchPerson("urn:collab:person:example.com:allen.ripe", "allen.ripe@example.com");
    testAddSearchPerson("allen.ripe", "allen.ripe@example.com");
  }

  private void testAddSearchPerson(String id, String email) {
    addPerson(createPerson(id, email));
    OAuthRequest req = new OAuthRequest(Verb.GET, getApiBaseUrl().concat(OS_URL).concat("people/").concat(id));
    String bodyText = getResult(req);
    assertTrue("response body should contain correct json data", bodyText.contains(email));
  }

  @Test
  public void testUserInjectionGroup() throws UniformInterfaceException, ClientHandlerException, IOException {
    String personId = "allen.ripe";
    addPerson(createPerson(personId, "allen.ripe@example.com"));

    String groupId = "urn:collab:group:example.com.group1";
    addGroup(new Group20(groupId, "title", "description"));
    String groupIdDummy = "dummyGroup2";
    addGroup(new Group20(groupIdDummy, "title", "description"));

    addPersonToGroup(personId, groupId);
    addPersonToGroup(personId, groupIdDummy);

    OAuthRequest req = new OAuthRequest(Verb.GET, getApiBaseUrl().concat(OS_URL).concat("groups/".concat(personId)));
    String bodyText = getResult(req);
    assertTrue("response body should contain correct json data", bodyText.contains("itemsPerPage\":2"));

    Client client = Client.create();
    client.addFilter(new HTTPBasicAuthFilter("okke", "password"));
    WebResource webResource = client.resource(getApiBaseUrl().concat(BASIC_URL).concat("groups/".concat(personId).concat("?startIndex=0&count=2147483647")));
    String response = IOUtils.toString(webResource.get(InputStream.class));
    assertTrue("response body should contain correct json data", response.contains("itemsPerPage\":2"));

  }

  @Test
  public void testUserInjectionMembers() {
    String groupId = "urn:collab:group:example.com.group1";
    addGroup(new Group20(groupId, "title", "description"));

    String[] personIds = { "person1", "person2", "person3" };
    for (String personId : personIds) {
      addPerson(createPerson(personId, personId.concat("@example.com")));
      addPersonToGroup(personId, groupId);
    }
    OAuthRequest req = new OAuthRequest(Verb.GET, String.format(getApiBaseUrl().concat(OS_URL).concat("people/%s/%s"),
        personIds[0], groupId));
    String bodyText = getResult(req);
    assertTrue("response body should contain correct json data", bodyText.contains("itemsPerPage\":3"));
    assertTrue(bodyText.contains("person1@example.com"));
    assertTrue(bodyText.contains("person2@example.com"));
    assertTrue(bodyText.contains("person3@example.com"));
  }

  private Person createPerson(String id, String email) {
    Person person = new Person();
    person.setId(id);
    person.setEmails(Collections.singleton(new Email(email)));
    return person;
  }

  private String getMockBaseUrl() {
    return getApiBaseUrl().concat("configure/");
  }

  protected String getApiBaseUrl() {
    return System.getProperty("selenium.test.url", "http://localhost:8095/api/");
  }

  private void reset() {
    apiCall(getMockBaseUrl().concat("reset"), "{}");
  }

  private void addPerson(Person person) {
    apiCall(getMockBaseUrl().concat("person"), toJson(person));
  }

  private void addGroup(Group20 group) {
    apiCall(getMockBaseUrl().concat("group"), toJson(group));
  }

  private void addPersonToGroup(String personId, String groupId) {
    apiCall(String.format(getMockBaseUrl().concat("person/").concat("%s/%s"), personId, groupId), "{}");
  }

  private String toJson(Object o) {
    try {
      return objectMapper.writeValueAsString(o);
    } catch (Exception e) {
      throw new RuntimeException("Exception in writing Json for MockExternalGroupProvider", e);
    }
  }

  private String getResult(OAuthRequest req) {
    service.signRequest(token, req);
    String bodyText = req.send().getBody();
    return bodyText;
  }

  private void apiCall(final String location, final String json) {
    final Client client = Client.create();
    final WebResource webResource = client.resource(location);
    final WebResource.Builder builder = webResource.accept("application/json").type("application/json");
    final ClientResponse response = builder.post(ClientResponse.class, json);
    final int status = response.getStatus();
    if (status < 200 || status >= 300) {
      throw new RuntimeException("Failed error in calling MockExternalGroupProvider, HTTP error code : " + status);
    }
  }
}