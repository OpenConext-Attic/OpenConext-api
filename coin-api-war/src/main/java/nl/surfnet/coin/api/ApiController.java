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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.api.oauth.ClientMetaData;
import nl.surfnet.coin.api.oauth.ExtendedBaseClientDetails;
import nl.surfnet.coin.api.oauth.ExtendedBaseConsumerDetails;
import nl.surfnet.coin.api.service.GroupService;
import nl.surfnet.coin.api.service.PersonService;
import nl.surfnet.coin.teams.domain.ServiceProviderGroupAcl;
import nl.surfnet.coin.teams.service.GroupProviderService;

public class ApiController extends AbstractApiController {

  private static Logger LOG = LoggerFactory.getLogger(ApiController.class);

  public static final String GROUP_ID_SELF = "@self";
  public static final String PERSON_ID_SELF = "@me";

  protected PersonService personService;

  protected GroupService groupService;

  @Resource(name = "groupProviderService")
  private GroupProviderService groupProviderService;

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

  public Object getGroupMembers(@PathVariable("userId") String userId,
                                           @PathVariable("groupId") String groupId,
                                           @RequestParam(value = "count",required = false) Integer count,
                                           @RequestParam(value = "startIndex", required = false) Integer startIndex,
                                           @RequestParam(value = "sortBy", required = false) String sortBy) {
    if (PERSON_ID_SELF.equals(userId)) {
      userId = getOnBehalfOf();
    }
    if (GROUP_ID_SELF.equals(groupId)) {
      // Backwards compatibility with os.surfconext.
      return getPerson(userId);
    }
    LOG.info("Got getGroupMembers-request, for userId '{}', groupId '{}', on behalf of '{}'", new Object[] { userId,
        groupId, getOnBehalfOf() });
    return personService.getGroupMembers(groupId, getOnBehalfOf(), count, startIndex, sortBy);
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

  /*
   * The two Service options for determining the correct ACL
   */
  private enum Service {
    People, Group
  }

  /*
   * Is the SP allowed to access the information based on the Group ACL's
   */
  private boolean isSpAllowed(Service service) {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ClientMetaData clientMetaData;
    if (principal instanceof ExtendedBaseClientDetails) {
      ExtendedBaseClientDetails clientDetails = (ExtendedBaseClientDetails) principal;
      clientMetaData = clientDetails.getClientMetaData();
    } else if (principal instanceof ExtendedBaseConsumerDetails) {
      ExtendedBaseConsumerDetails consumerDetails = (ExtendedBaseConsumerDetails) principal;
      clientMetaData = consumerDetails.getClientMetaData();
    } else {
      throw new RuntimeException(
          "The Principal from 'SecurityContextHolder.getContext().getAuthentication().getPrincipal()' is an unknown instance("
              + (principal != null ? principal.getClass() : "null") + ")");
    }
    Assert.notNull(clientMetaData,"ClientMetaData may not be null for checking ACL's");
    List<ServiceProviderGroupAcl> acls = groupProviderService.getServiceProviderGroupAcl(clientMetaData.getAppEntityId());
    for (ServiceProviderGroupAcl acl : acls) {
      
    }
    return true;
  }
}
