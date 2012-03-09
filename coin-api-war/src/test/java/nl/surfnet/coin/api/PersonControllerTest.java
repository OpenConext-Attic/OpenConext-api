package nl.surfnet.coin.api;

import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.service.PersonService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;
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
        when(personService.getPerson("foo", "loggedInUser")).thenReturn(p);
        Person personReturned = pc.getPerson("foo", "@self", "loggedInUser");
        assertEquals("id", personReturned.getId());
    }
}
