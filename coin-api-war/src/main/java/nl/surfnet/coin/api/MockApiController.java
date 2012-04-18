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

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.api.service.GroupService;

import nl.surfnet.coin.api.service.PersonService;

import static nl.surfnet.coin.api.PersonController.getOnBehalfOf;

/**
 * Controller for the mock REST interface.
 */
@Controller
@RequestMapping(value = "mock10/social/rest")
public class MockApiController {

  private static Logger LOG = LoggerFactory.getLogger(MockApiController.class);

  @Resource(name="mockService")
  private PersonService personService;

  @Resource(name="mockService")
  private GroupService groupService;

  @RequestMapping(value = "/people/{userId}/@self")
  @ResponseBody
  public PersonEntry getPersonAtSelf(@PathVariable("userId") String userId) {
    return getPerson(userId);
  }

  @RequestMapping(value = "/people/{userId}")
  @ResponseBody
  public PersonEntry getPerson(@PathVariable("userId")
  String userId) {

    LOG.info("Got getPerson-request, for userId '{}' on behalf of '{}'",
        new Object[] { userId, PersonController.getOnBehalfOf() });
    return personService.getPerson(userId, PersonController.getOnBehalfOf());
  }

  @RequestMapping(value = "/people/{userId}/{groupId}")
  @ResponseBody
  public GroupMembersEntry getGroupMembers(@PathVariable("userId")
  String userId, @PathVariable("groupId")
  String groupId) {

    LOG.info("Got getGroupMembers-request, for userId '{}', groupId '{}', on behalf of '{}'", new Object[] { userId,
        groupId, PersonController.getOnBehalfOf() });
    return personService.getGroupMembers(groupId, PersonController.getOnBehalfOf());
  }

  @RequestMapping(value = "/groups/{userId}")
  @ResponseBody
  public Group20Entry getGroups(@PathVariable("userId")
  String userId) {

    LOG.info("Got getGroups-request, for userId '{}',  on behalf of '{}'",
        new Object[] { userId, PersonController.getOnBehalfOf() });
    return groupService.getGroups20(userId, PersonController.getOnBehalfOf());
  }

 

}
