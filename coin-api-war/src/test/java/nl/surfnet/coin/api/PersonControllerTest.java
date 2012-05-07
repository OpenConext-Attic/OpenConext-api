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
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.api.oauth.ClientMetaData;
import nl.surfnet.coin.api.oauth.ExtendedBaseConsumerDetails;
import nl.surfnet.coin.api.service.MockServiceImpl;
import nl.surfnet.coin.api.service.PersonService;
import nl.surfnet.coin.db.AbstractInMemoryDatabaseTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:coin-api-properties-context.xml", "classpath:coin-api-context.xml",
    "classpath:coin-api-oauth1-context.xml", "classpath:coin-api-oauth2-context.xml",
    "classpath:coin-shared-context.xml" })
public class PersonControllerTest {

  @Autowired
  @InjectMocks
  PersonController pc;

  @Mock
  private PersonService personService;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    pc.groupProviderConfiguration = new MockServiceImpl();
  }

  @Test
  public void getPerson() {
    //final Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.getContext().setAuthentication(getAuthentication());
    //when(authentication.getPrincipal()).thenReturn("foo");

    Person p = new Person();
    p.setId("urn:collab:person:test.surfguest.nl:bar");
    PersonEntry entry = new PersonEntry();
    entry.setEntry(p);
    when(personService.getPerson("foo", null)).thenReturn(entry);
    Person personReturned = pc.getPerson("foo").getEntry();
    assertEquals("urn:collab:person:test.surfguest.nl:bar", personReturned.getId());

    Person person2Returned = ((PersonEntry) pc.getGroupMembers("foo", "@self", 1, 0, "")).getEntry();
    assertEquals("urn:collab:person:test.surfguest.nl:bar", personReturned.getId());

  }

  private Authentication getAuthentication() {
    ExtendedBaseConsumerDetails consumerDetails = new ExtendedBaseConsumerDetails();
    consumerDetails.setClientMetaData(new ClientMetaData());
    ConsumerCredentials consumerCredentials = new ConsumerCredentials("consumerKey", "signature", "signatureMethod",
        "signatureBaseString", "token");
    ConsumerAuthentication conAuth = new ConsumerAuthentication(consumerDetails, consumerCredentials);
    return conAuth;
  }

}
