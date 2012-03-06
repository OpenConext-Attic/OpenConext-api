package nl.surfnet.coin.api;

import nl.surfnet.coin.opensocial.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class PersonController {

    @RequestMapping(value="/social/groups", method=GET)
    public String getGroups() {
        return null;
    }
}
