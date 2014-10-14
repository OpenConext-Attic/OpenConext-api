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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.api.oauth.ClientMetaData;
import nl.surfnet.coin.api.oauth.JanusClientMetadata;
import nl.surfnet.coin.api.service.MockServiceImpl;
import nl.surfnet.coin.api.service.OpenConextClientDetailsService;
import nl.surfnet.coin.eb.EngineBlock;
import nl.surfnet.coin.janus.domain.EntityMetadata;
import nl.surfnet.coin.shared.log.ApiCallLog;

/**
 * Controller for the mock REST interface.
 * 
 */
@Controller
@RequestMapping(value = { "mock10/social/rest", "mockbasic/social/rest" })
public class MockApiController extends ApiController {

  private static Logger LOG = LoggerFactory.getLogger(MockApiController.class);

  @Value("${mock-api-enabled}")
  private boolean mockApiEnabled;

  public MockApiController() {
    MockServiceImpl impl = new MockServiceImpl();
    this.personService = impl;
    this.groupProviderConfiguration = impl;
    this.groupService = impl;
  }

  @Override
  public void invariant() {
    if (!this.mockApiEnabled) {
      throw new RuntimeException("Mock API not enabled");
    }
  }

  @Resource(name = "engineBlock")
  public void setEngineBlock(EngineBlock engineBlock) {
    this.engineBlock = engineBlock;
  }
  
  @Resource(name ="janusClientDetailsService")
  public void setJanusClientDetailsService(OpenConextClientDetailsService janusClient) {
    this.janusClientDetailsService = janusClient;
  }

  public void setMockApiEnabled(boolean mockApiEnabled) {
    this.mockApiEnabled = mockApiEnabled;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/groups/{userId:.+}/{groupId}")
  @ResponseBody
  @Override
  public Group20Entry getGroup(@PathVariable("userId")
  String userId, @PathVariable("groupId")
  String groupId) {
    invariant();
    String onBehalfOf = getOnBehalfOf();
    if (PERSON_ID_SELF.equals(userId)) {
      userId = onBehalfOf;
    }
    if (onBehalfOf == null) {
      onBehalfOf = userId;
    }
    String spEntityId = getClientMetaData().getAppEntityId();
    ensureAccess(spEntityId, groupId, userId);
    /*
     * As an external Mock provider we don't care which groupProviders we have.
     * We want to return either the json pre-configured groups or the injected
     * groups
     */
    Group20Entry group20Entry = groupService.getGroup20(userId, groupId, onBehalfOf);

    logApiCall(onBehalfOf);
    setResultOptions(group20Entry, 0, 0, null);
    return group20Entry;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/people/{userId:.+}")
  @ResponseBody
  @Override
  public PersonEntry getPerson(@PathVariable("userId")
  String userId) {
    String onBehalfOf = getOnBehalfOf();
    LOG.info("Got getPerson-request, for userId '{}' on behalf of '{}'", new Object[] { userId, onBehalfOf });
    if (PERSON_ID_SELF.equals(userId)) {
      userId = onBehalfOf;
    }
    String spEntityId = getClientMetaData().getAppEntityId();
    PersonEntry person = personService.getPerson(userId, onBehalfOf, spEntityId);
    logApiCall(onBehalfOf);
    setResultOptions(person, 0, 0, null);
    return person;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/groups/{userId:.+}")
  @ResponseBody
  @Override
  public Group20Entry getGroups(@PathVariable("userId")
  String userId, @RequestParam(value = "count", required = false)
  Integer count, @RequestParam(value = "startIndex", required = false)
  Integer startIndex, @RequestParam(value = "sortBy", required = false)
  String sortBy) {
    invariant();
    String onBehalfOf = getOnBehalfOf();
    if (PERSON_ID_SELF.equals(userId)) {
      userId = onBehalfOf;
    }
    LOG.info("Got getGroups-request, for userId '{}',  on behalf of '{}'", new Object[] { userId, onBehalfOf });
    String spEntityId = getClientMetaData().getAppEntityId();
    Group20Entry group20Entry = groupService.getGroups20(userId, onBehalfOf, count, startIndex, sortBy);
    filterGroups(spEntityId, group20Entry);
    logApiCall(onBehalfOf);
    setResultOptions(group20Entry, count, startIndex, sortBy);
    return group20Entry;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/people/{userId:.+}/{groupId:.+}")
  @ResponseBody
  @Override
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
    if (onBehalfOf == null) {
      onBehalfOf = userId;
    }
    LOG.info("Got getGroupMembers-request, for userId '{}', groupId '{}', on behalf of '{}'", new Object[] { userId,
        groupId, onBehalfOf });
    String spEntityId = getClientMetaData().getAppEntityId();
    ensureAccess(spEntityId, groupId, userId);
    GroupMembersEntry groupMembers = personService.getGroupMembers(groupId, onBehalfOf, spEntityId, count, startIndex,
        sortBy);
    logApiCall(onBehalfOf);
    setResultOptions(groupMembers, count, startIndex, sortBy);
    return groupMembers;
  }

  /**
   * Hook for subclasses to change the log record
   * 
   * @param log
   */

  protected void addApiCallLogInfo(ApiCallLog log) {
    log.setApiVersion("MOCK");
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.api.AbstractApiController#getClientMetaData()
   */
  @Override
  protected ClientMetaData getClientMetaData() {
    try {
      return super.getClientMetaData();
    } catch (IllegalArgumentException e) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      // basic
      if (authentication instanceof UsernamePasswordAuthenticationToken) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        EntityMetadata metaData = new EntityMetadata();
        metaData.setAppEntityId("DUMMY-BASIC-AUTH");
        metaData.setOauthConsumerKey(token.getPrincipal() + ":" + token.getCredentials());
        JanusClientMetadata clientMetadata = new JanusClientMetadata(metaData);
        return clientMetadata;
      } else {
        throw e;
      }
    }
  }

}
