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

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.api.service.GroupService;
import nl.surfnet.coin.api.service.PersonService;

/**
 * Controller for the person REST interface..
 */
@Controller
@RequestMapping(value = "/social/rest")
public class PersonController extends AbstractApiController {

  private static Logger LOG = LoggerFactory.getLogger(PersonController.class);

  public static final String GROUP_ID_SELF = "@self";
  public static final String PERSON_ID_SELF = "@me";

  @Resource(name="ldapService")
  private PersonService personService;

  @Resource(name="groupService")
  private GroupService groupService;

  @RequestMapping(method=RequestMethod.GET, value = "/people/{userId:.+}/{groupId}")
  @ResponseBody
  public PersonEntry getPerson(
      @PathVariable("userId") String userId,
      @PathVariable("groupId") String groupId) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Got getPerson-request, for userId '{}', groupId '{}', on behalf of '{}'", new Object[]{userId, groupId, getOnBehalfOf()});
    }
    if (GROUP_ID_SELF.equals(groupId)) {
      if (PERSON_ID_SELF.equals(userId)) {
        userId = getOnBehalfOf();
      }
      return personService.getPerson(userId, getOnBehalfOf());
    } else {
      throw new UnsupportedOperationException("Not supported: person query with group other than @self.");
    }
  }

  @RequestMapping(method=RequestMethod.GET, value = "/people/{userId:.+}")
  @ResponseBody
  public PersonEntry getPerson(@PathVariable("userId") String userId) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Got getPerson-request, for userId '{}' on behalf of '{}'", new Object[]{userId, getOnBehalfOf()});
    }
    if (PERSON_ID_SELF.equals(userId)) {
      userId = getOnBehalfOf();
    }
    return personService.getPerson(userId, getOnBehalfOf());
  }


  @RequestMapping(method=RequestMethod.GET, value = "/groups/{userId:.+}")
  @ResponseBody
  public Group20Entry getGroups(@PathVariable("userId")
                                String userId, @RequestParam(value = "count", required = false)
  Integer count, @RequestParam(value = "startIndex", required = false)
  Integer startIndex, @RequestParam(value = "sortBy", required = false)
  String sortBy) {
    if (PersonController.PERSON_ID_SELF.equals(userId)) {
      userId = getOnBehalfOf();
    }
    LOG.info("Got getGroups-request, for userId '{}',  on behalf of '{}'",
        new Object[] { userId, getOnBehalfOf() });
    Group20Entry groups = groupService.getGroups20(userId, getOnBehalfOf());
    List<Group20> entry = groups.getEntry();
    processQueryOptions(groups, count, startIndex, sortBy, entry);
    return groups;
  }
}
