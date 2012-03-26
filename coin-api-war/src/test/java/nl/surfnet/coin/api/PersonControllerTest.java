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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.api.service.PersonService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:coin-api-properties-context.xml",
        "classpath:coin-api-context.xml"})
public class PersonControllerTest {

    @Autowired
    @InjectMocks
    PersonController pc;

    @Mock
    private PersonService personService;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getPersonInGroupNotSupported() {
        pc.getPerson("foo", "bar", "loggedInUser");
    }

    @Test
    public void getPerson() {
        Person p = new Person();
        p.setId("id");
        PersonEntry entry = new PersonEntry();
        entry.setEntry(p);
        when(personService.getPerson("foo", "loggedInUser")).thenReturn(entry);
        Person personReturned = pc.getPerson("foo", "@self", "loggedInUser").getEntry();
        assertEquals("urn:collab:person:test.surfguest.nl:mfoo", personReturned.getId());
    }
}
