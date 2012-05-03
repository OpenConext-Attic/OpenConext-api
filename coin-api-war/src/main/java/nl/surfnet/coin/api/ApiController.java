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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import nl.surfnet.coin.api.GroupProviderConfiguration.Service;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.api.service.GroupService;
import nl.surfnet.coin.api.service.PersonService;
import nl.surfnet.coin.eb.EngineBlock;
import nl.surfnet.coin.ldap.LdapClient;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.service.OauthGroupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

;

public class ApiController extends AbstractApiController {

  private static Logger LOG = LoggerFactory.getLogger(ApiController.class);

  public static final String GROUP_ID_SELF = "@self";
  public static final String PERSON_ID_SELF = "@me";

  protected PersonService personService;

  protected GroupService groupService;

  protected EngineBlock engineBlock;

  protected GroupProviderConfiguration groupProviderConfiguration;

  @RequestMapping(method = RequestMethod.GET, value = "/people/{userId:.+}")
  @ResponseBody
  public PersonEntry getPerson(@PathVariable("userId")
  String userId) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Got getPerson-request, for userId '{}' on behalf of '{}'", new Object[] { userId, getOnBehalfOf() });
    }
    if (PERSON_ID_SELF.equals(userId)) {
      userId = getOnBehalfOf();
    }
    return personService.getPerson(userId, getOnBehalfOf());
  }

  @RequestMapping(method = RequestMethod.GET, value = "/people/{userId:.+}/{groupId:.+}")
  @ResponseBody
  public Object getGroupMembers(@PathVariable("userId")
  String userId, @PathVariable("groupId")
  String groupId, @RequestParam(value = "count", required = false)
  Integer count, @RequestParam(value = "startIndex", required = false)
  Integer startIndex, @RequestParam(value = "sortBy", required = false)
  String sortBy) {
    String onBehalfOf = getOnBehalfOf();
    if (PERSON_ID_SELF.equals(userId)) {
      userId = onBehalfOf;
    }
    if (GROUP_ID_SELF.equals(groupId)) {
      // Backwards compatibility with os.surfconext.
      return getPerson(userId);
    }
    if (onBehalfOf != null && !onBehalfOf.startsWith(LdapClient.URN_IDENTIFIER)) {
      throw new RuntimeException("It is not allowed to use a different identifier (" + onBehalfOf
          + ") then @me when retrieving groupMembers");
    }
    LOG.info("Got getGroupMembers-request, for userId '{}', groupId '{}', on behalf of '{}'", new Object[] { userId,
        groupId, onBehalfOf });
    /*
     * Is the call to Grouper allowed?
     */
    List<GroupProvider> allGroupProviders = groupProviderConfiguration.getAllGroupProviders();
    String spEntityId = getClientMetaData().getAppEntityId();
    boolean grouperAllowed = groupProviderConfiguration.isGrouperCallsAllowed(Service.People, spEntityId,
        allGroupProviders);
    // sensible default
    GroupMembersEntry groupMembers = new GroupMembersEntry(new ArrayList<Person>());
    /*
     * Is the call to Grouper necessary (e.g. is this an internal group)?
     */
    if (groupProviderConfiguration.isInternalGroup(groupId)) {
      if (grouperAllowed) {
        groupMembers = personService.getGroupMembers(groupId, onBehalfOf, count, startIndex, sortBy);
      }
    } else {
      // external group. see which groupProvider can handle this call
      List<GroupProvider> allowedGroupProviders = groupProviderConfiguration.getAllowedGroupProviders(Service.People,
          spEntityId, allGroupProviders);
      for (GroupProvider groupProvider : allowedGroupProviders) {
        /*
         * Do we need to make calls this external group provider?
         */
        if (groupProvider.isMeantForUser(onBehalfOf)) {
          GroupMembersEntry externalGroupMembers = groupProviderConfiguration.getGroupMembersEntry(groupProvider,
              onBehalfOf, groupId, count == null ? Integer.MAX_VALUE : count, startIndex == null ? 0 : startIndex);
          if (externalGroupMembers != null) {
            List<Person> entry = externalGroupMembers.getEntry();
            if (entry != null) {
              groupMembers.getEntry().addAll(entry);
              groupMembers.setTotalResults(groupMembers.getTotalResults() + entry.size());
            }
          }
        }
      }
    }
    return groupMembers;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/groups/{userId:.+}")
  @ResponseBody
  public Group20Entry getGroups(@PathVariable("userId")
  String userId, @RequestParam(value = "count", required = false)
  Integer count, @RequestParam(value = "startIndex", required = false)
  Integer startIndex, @RequestParam(value = "sortBy", required = false)
  String sortBy) {
    invariant();
    if (PERSON_ID_SELF.equals(userId)) {
      userId = getOnBehalfOf();
    }
    LOG.info("Got getGroups-request, for userId '{}',  on behalf of '{}'", new Object[] { userId, getOnBehalfOf() });
    return groupService.getGroups20(userId, getOnBehalfOf(), count, startIndex, sortBy);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/groups/{userId:.+}/{groupId}")
  @ResponseBody
  public Group20Entry getGroup(@PathVariable("userId")
  String userId, @PathVariable("groupId")
  String groupId) {
    invariant();
    if (PERSON_ID_SELF.equals(userId)) {
      userId = getOnBehalfOf();
    }
    return groupService.getGroup20(userId, groupId, getOnBehalfOf());
  }

  protected void invariant() {
  }

}
