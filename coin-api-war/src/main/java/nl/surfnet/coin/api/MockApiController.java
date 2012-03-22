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

import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.api.service.MockService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for the mock REST interface..
 */
@Controller
@RequestMapping(value = "mock10")
public class MockApiController {

  private static Logger LOG = LoggerFactory.getLogger(MockApiController.class);

  private MockService mockService = new MockService();

  @RequestMapping(value = "/social/rest/people/{userId}/@self")
  @ResponseBody
  public PersonEntry getPersonAtSelf(@PathVariable("userId") String userId,
  // for now, a cookie with the onBehalfOf-user is required. Will probably
  // change to a SessionAttribute set by a filter, when OAuth is in place.
      @CookieValue(value = "onBehalfOf", required = false) String onBehalfOf) {
    return getPerson(userId, onBehalfOf);
  }

  @RequestMapping(value = "/social/rest/people/{userId}")
  @ResponseBody
  public PersonEntry getPerson(@PathVariable("userId") String userId,
  // for now, a cookie with the onBehalfOf-user is required. Will probably
  // change to a SessionAttribute set by a filter, when OAuth is in place.
      @CookieValue(value = "onBehalfOf", required = false) String onBehalfOf) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Got getPerson-request, for userId '{}' on behalf of '{}'",
          new Object[] { userId, onBehalfOf });
    }
    return mockService.getPerson(userId, onBehalfOf);
  }

  @RequestMapping(value = "/social/rest/people/{userId}/{groupId}")
  @ResponseBody
  public GroupMembersEntry getGroupMembers(
      @PathVariable("userId") String userId,
      @PathVariable("groupId") String groupId,
      // for now, a cookie with the onBehalfOf-user is required. Will probably
      // change to a SessionAttribute set by a filter, when OAuth is in place.
      @CookieValue(value = "onBehalfOf", required = false) String onBehalfOf) {
    if (LOG.isDebugEnabled()) {
      LOG.debug(
          "Got getGroupMembers-request, for userId '{}', groupId '{}', on behalf of '{}'",
          new Object[] { userId, groupId, onBehalfOf });
    }
    return mockService.getGroupMembers(groupId, onBehalfOf);
  }

  @RequestMapping(value = "/social/rest/groups/{userId}")
  @ResponseBody
  public Group20Entry getGroups(@PathVariable("userId") String userId,

  // for now, a cookie with the onBehalfOf-user is required. Will probably
  // change to a SessionAttribute set by a filter, when OAuth is in place.
      @CookieValue(value = "onBehalfOf", required = false) String onBehalfOf) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Got getGroups-request, for userId '{}',  on behalf of '{}'",
          new Object[] { userId, onBehalfOf });
    }
    return mockService.getGroups20(userId, onBehalfOf);
  }

}
