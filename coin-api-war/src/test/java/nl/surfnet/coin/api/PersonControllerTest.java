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

package nl.surfnet.coin.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth.provider.ConsumerAuthentication;
import org.springframework.security.oauth.provider.ConsumerCredentials;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.api.oauth.OpenConextConsumerDetails;
import nl.surfnet.coin.api.oauth.JanusClientMetadata;
import nl.surfnet.coin.api.service.GroupService;
import nl.surfnet.coin.api.service.MockServiceImpl;
import nl.surfnet.coin.api.service.PersonService;
import nl.surfnet.coin.janus.domain.EntityMetadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
  "classpath:coin-api-properties-context.xml",
  "classpath:coin-api-context.xml",
  "classpath:coin-api-oauth1-context.xml",
  "classpath:coin-api-oauth2-context.xml",
  "classpath:coin-shared-context.xml"
})
@ActiveProfiles("openconext")
public class PersonControllerTest {

  @Autowired
  @InjectMocks
  PersonController pc;

  @Mock
  private PersonService personService;

  @Mock
  private GroupService groupService;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    pc.groupProviderConfiguration = new MockServiceImpl();
    SecurityContextHolder.getContext().setAuthentication(getAuthentication());
  }

  @Test
  public void getPerson() {
    Person p = new Person();
    p.setId("urn:collab:person:test.surfguest.nl:bar");
    PersonEntry entry = new PersonEntry();
    entry.setEntry(p);
    when(personService.getPerson("foo", null, null)).thenReturn(entry);
    Person personReturned = pc.getPerson("foo").getEntry();
    assertEquals("urn:collab:person:test.surfguest.nl:bar", personReturned.getId());

    Person person2Returned = ((PersonEntry) pc.getGroupMembers("foo", "@self", 1, 0, "")).getEntry();
    assertEquals("urn:collab:person:test.surfguest.nl:bar", person2Returned.getId());

  }

/*
 // This is a test for BACKLOG-479
  @Test
  public void getGroups() {
  when(groupService.getGroups20(anyString(), anyString(), (Integer) eq(null),(Integer) eq(null),(String) eq(null)))
      .thenReturn(new Group20Entry(new ArrayList<Group20>()));
    final Group20Entry groups = pc.getGroups("urn:collab:person:test.surfguest.nl:foobar", null, null, null);
    assertNotNull(groups);
  }
*/

  private Authentication getAuthentication() {
    OpenConextConsumerDetails consumerDetails = new OpenConextConsumerDetails();
    final JanusClientMetadata clientMetaData = new JanusClientMetadata(new EntityMetadata());
    consumerDetails.setClientMetaData(clientMetaData);
    ConsumerCredentials consumerCredentials = new ConsumerCredentials("consumerKey", "signature", "signatureMethod",
      "signatureBaseString", "token");
    return new ConsumerAuthentication(consumerDetails, consumerCredentials);
  }

}
