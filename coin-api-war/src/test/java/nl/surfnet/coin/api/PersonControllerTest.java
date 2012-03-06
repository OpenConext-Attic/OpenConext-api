package nl.surfnet.coin.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:coin-api-properties-context.xml", "classpath:coin-api-context.xml"})
public class PersonControllerTest {

    @Autowired PersonController p;
    @Test
    public void getGroups() {
        assertNull(p.getGroups("foo", "bar"));
    }
}
