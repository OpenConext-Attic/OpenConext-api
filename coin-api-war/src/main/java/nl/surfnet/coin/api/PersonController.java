package nl.surfnet.coin.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PersonController {

    private static Logger LOG = LoggerFactory.getLogger(PersonController.class);

    @RequestMapping(value = "/social/people/{userId}/{groupId}")
    @ResponseBody
    public String getPerson(@PathVariable("userId") String userId, @PathVariable("groupId") String groupId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got getGroups-request, for userId '{}', groupId '{}'", userId, groupId);
        }
        return "hallo";
    }
}
