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

import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for the person REST interface..
 */
@Controller

public class PersonController {

    private static Logger LOG = LoggerFactory.getLogger(PersonController.class);

    private static final String GROUP_ID_SELF = "@self";

    @Autowired
    private PersonService personService;

    @RequestMapping(value = "/rest/people/{userId}/{groupId}")
    @ResponseBody
    public Person getPerson(
            @PathVariable("userId") String userId,
            @PathVariable("groupId") String groupId,
            // for now, a cookie with the onBehalfOf-user is required. Will probably change to a SessionAttribute set by a filter, when OAuth is in place.
            @CookieValue(value="onBehalfOf", required = false) String onBehalfOf) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got getPerson-request, for userId '{}', groupId '{}', on behalf of '{}'", new Object[] {userId, groupId, onBehalfOf});
        }
        if (GROUP_ID_SELF.equals(groupId)) {
            return personService.getPerson(userId, onBehalfOf);
        } else {
            throw new UnsupportedOperationException("Not supported: person query other than @self.");
        }
    }
}
