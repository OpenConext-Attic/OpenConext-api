package nl.surfnet.coin.api;

import nl.surfnet.coin.api.client.domain.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:coin-api-properties-context.xml",
    "classpath:coin-api-context.xml" })
public class PersonControllerTest {

  @Autowired
  PersonController p;

  @Test(expected = UnsupportedOperationException.class)
  public void getPersonInGroupNotSupported() {
    p.getPerson("foo", "bar", "loggedInUser");
  }

  @Test
  public void getPerson() {
      final Person person = p.getPerson("foo", "@self", "loggedInUser");
      assertEquals("foo", person.getName());
  }
}
