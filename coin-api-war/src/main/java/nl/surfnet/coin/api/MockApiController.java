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

import static nl.surfnet.coin.api.PersonController.getOnBehalfOf;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.api.service.GroupService;
import nl.surfnet.coin.api.service.PersonService;

/**
 * Controller for the mock REST interface.
 */
@Controller
@RequestMapping(value = "mock10/social/rest")
@SuppressWarnings("unchecked")
public class MockApiController extends AbstractApiController {

  private static Logger LOG = LoggerFactory.getLogger(MockApiController.class);

  @Value("${mock-api-enabled}")
  private boolean mockApiEnabled;
  
  @Resource(name = "mockService")
  private PersonService personService;

  @Resource(name = "mockService")
  private GroupService groupService;

  @RequestMapping(value = "/people/{userId}/@self")
  @ResponseBody
  public PersonEntry getPersonAtSelf(@PathVariable("userId")
  String userId) {
    return getPerson(userId);
  }

  @RequestMapping(value = "/people/{userId}")
  @ResponseBody
  public PersonEntry getPerson(@PathVariable("userId")
  String userId) {
    invariant();
    LOG.info("Got getPerson-request, for userId '{}' on behalf of '{}'",
        new Object[] { userId, getOnBehalfOf() });
    if (PersonController.PERSON_ID_SELF.equals(userId)) {
      userId = getOnBehalfOf();
    }
    return personService.getPerson(userId, getOnBehalfOf());
  }

  @RequestMapping(value = "/people/{userId}/{groupId}")
  @ResponseBody
  public GroupMembersEntry getGroupMembers(@PathVariable("userId")
  String userId, @PathVariable("groupId")
  String groupId, @RequestParam(value = "count", required = false)
  Integer count, @RequestParam(value = "startIndex", required = false)
  Integer startIndex, @RequestParam(value = "sortBy", required = false)
  String sortBy) {
    invariant();
    if (PersonController.PERSON_ID_SELF.equals(userId)) {
      userId = getOnBehalfOf();
    }
    LOG.info("Got getGroupMembers-request, for userId '{}', groupId '{}', on behalf of '{}'", new Object[] { userId,
        groupId, getOnBehalfOf() });
    GroupMembersEntry groupMembers = personService.getGroupMembers(groupId, getOnBehalfOf());
    List<Person> entry = groupMembers.getEntry();
    entry = (List<Person>) processQueryOptions(groupMembers, count, startIndex, sortBy, entry);
    groupMembers.setEntry(entry);
    return groupMembers;
  }

  @RequestMapping(value = "/groups/{userId}")
  @ResponseBody
  public Group20Entry getGroups(@PathVariable("userId")
  String userId, @RequestParam(value = "count", required = false)
  Integer count, @RequestParam(value = "startIndex", required = false)
  Integer startIndex, @RequestParam(value = "sortBy", required = false)
  String sortBy) {
    invariant();
    if (PersonController.PERSON_ID_SELF.equals(userId)) {
      userId = getOnBehalfOf();
}
    LOG.info("Got getGroups-request, for userId '{}',  on behalf of '{}'",
        new Object[] { userId, PersonController.getOnBehalfOf() });
    Group20Entry groups = groupService.getGroups20(userId, PersonController.getOnBehalfOf());
    List<Group20> entry = groups.getEntry();
    entry = (List<Group20>) processQueryOptions(groups, count, startIndex, sortBy, entry);
    return groups;
  }

  private void invariant() {
    if (!this.mockApiEnabled) {
      throw new RuntimeException("Mock API not enabled");
    }
    
  }

  
}
