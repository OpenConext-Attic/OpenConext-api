package nl.surfnet.coin.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for the person REST interface..
 */
@Controller
public class PersonController {

    private static Logger LOG = LoggerFactory.getLogger(PersonController.class);

    private static final String GROUP_ID_SELF = "@self";

    @RequestMapping(value = "/social/people/{userId}/{groupId}")
    @ResponseBody
    public String getPerson(@PathVariable("userId") String userId, @PathVariable("groupId") String groupId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got getPerson-request, for userId '{}', groupId '{}'", userId, groupId);
        }
        if (GROUP_ID_SELF.equals(groupId)) {
            return "person " + userId;
        } else {
            return "people in " + groupId;
        }
    }
}
