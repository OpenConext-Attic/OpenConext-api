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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import nl.surfnet.coin.api.GroupProviderConfiguration.Service;
import nl.surfnet.coin.api.client.domain.AbstractEntry;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupEntry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.api.oauth.ClientMetaData;
import nl.surfnet.coin.api.service.GroupService;
import nl.surfnet.coin.api.service.PersonService;
import nl.surfnet.coin.eb.EngineBlock;
import nl.surfnet.coin.ldap.LdapClient;
import nl.surfnet.coin.shared.domain.ErrorMail;
import nl.surfnet.coin.shared.log.ApiCallLog;
import nl.surfnet.coin.shared.log.ApiCallLogContextListener;
import nl.surfnet.coin.shared.log.ApiCallLogService;
import nl.surfnet.coin.shared.service.ErrorMessageMailer;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderType;
import nl.surfnet.coin.teams.domain.TeamExternalGroup;
import nl.surfnet.coin.teams.service.TeamExternalGroupDao;

/**
 * 
 * We wildcard all {@link RequestMapping} with ':.+' (see
 * http://stackoverflow.com
 * /questions/3526523/spring-mvc-pathvariable-getting-truncated)
 * 
 */
public class ApiController extends AbstractApiController {

  private static Logger LOG = LoggerFactory.getLogger(ApiController.class);

  public static final String GROUP_ID_SELF = "@self";
  public static final String PERSON_ID_SELF = "@me";

  protected PersonService personService;

  protected GroupService groupService;

  protected EngineBlock engineBlock;

  protected GroupProviderConfiguration groupProviderConfiguration;

  @Autowired
  protected TeamExternalGroupDao teamExternalGroupDao;

  @Resource(name = "errorMessageMailer")
  private ErrorMessageMailer errorMessageMailer;

  @Autowired
  private ApiCallLogService logService;

  @RequestMapping(method = RequestMethod.GET, value = "/people/{userId:.+}")
  @ResponseBody
  public PersonEntry getPerson(@PathVariable("userId")
  String userId) {
    String onBehalfOf = getOnBehalfOf();
    LOG.info("Got getPerson-request, for userId '{}' on behalf of '{}'", new Object[] { userId, onBehalfOf });
    if (PERSON_ID_SELF.equals(userId)) {
      userId = onBehalfOf;
    }
    List<GroupProvider> allGroupProviders = getAllAllowedGroupProviders(Service.People);
    GroupProvider grouper = getGrouperProvider(allGroupProviders);
    String spEntityId = getClientMetaData().getAppEntityId();
    // sensible default
    PersonEntry person = new PersonEntry();
    if (!groupProviderConfiguration.isCallAllowed(Service.People, spEntityId, grouper)) {
      sendAclMissingMail(grouper, spEntityId, userId, Service.People);
    } else {
      person = personService.getPerson(userId, onBehalfOf, spEntityId);
    }
    logApiCall(onBehalfOf);
    setResultOptions(person, 0, 0, null);
    return person;
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
    String spEntityId = getClientMetaData().getAppEntityId();
    if (!userId.startsWith(LdapClient.URN_IDENTIFIER)) {
      // persistent identifier, need urn to query
      PersonEntry person = personService.getPerson(userId, onBehalfOf, spEntityId);
      userId = person.getEntry().getId();
    }
    if (onBehalfOf == null) {
      onBehalfOf = userId;
    }
    LOG.info("Got getGroupMembers-request, for userId '{}', groupId '{}', on behalf of '{}'", new Object[] { userId,
        groupId, onBehalfOf });
    List<GroupProvider> allGroupProviders = getAllAllowedGroupProviders(Service.People);
    // sensible default
    GroupMembersEntry groupMembers = new GroupMembersEntry(new ArrayList<Person>());
    GroupProvider grouper = getGrouperProvider(allGroupProviders);
    boolean grouperAllowed = groupProviderConfiguration.isCallAllowed(Service.People, spEntityId, grouper);
    boolean internalGroup = groupProviderConfiguration.isInternalGroup(groupId);
    if (!grouperAllowed) {
      sendAclMissingMail(grouper, spEntityId, userId, Service.People);
    }
    if (grouperAllowed && internalGroup) {
      // need to cut off the urn part in order for Grouper
      String grouperGroupId = groupProviderConfiguration.cutOffUrnPartForGrouper(allGroupProviders, groupId);
      groupMembers = personService.getGroupMembers(grouperGroupId, onBehalfOf, spEntityId, count, startIndex, sortBy);
    }
    if (!internalGroup) {
      // external group. see which groupProvider can handle this call
      for (GroupProvider groupProvider : allGroupProviders) {
        /*
         * Do we need to make calls to this external group provider?
         */
        if (groupProvider.isExternalGroupProvider() && groupProvider.isMeantForUser(onBehalfOf)) {
          GroupMembersEntry externalGroupMembers = groupProviderConfiguration.getGroupMembersEntry(groupProvider,
              onBehalfOf, groupId, count == null ? Integer.MAX_VALUE : count, startIndex == null ? 0 : startIndex);
          if (externalGroupMembers != null) {
            List<Person> entry = externalGroupMembers.getEntry();
            if (entry != null) {
              groupMembers.getEntry().addAll(entry);
            }
          }
        }
      }
      /*
       * Is this an external group that is linked to a SURFteams group?
       */
      Group20Entry grouperTeams = getGrouperTeamsLinkedToExternalGroup(userId, groupId);
      if (grouperTeams != null && grouperTeams.getEntry() != null && !grouperTeams.getEntry().isEmpty()) {
        for (Group20 group20 : grouperTeams.getEntry()) {
          GroupMembersEntry members = personService.getGroupMembers(group20.getId(), userId, spEntityId, null, null,
              null);
          groupMembers.getEntry().addAll(members.getEntry());
        }
      }
    }
    logApiCall(onBehalfOf);
    setResultOptions(groupMembers, count, startIndex, sortBy);
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
    String onBehalfOf = getOnBehalfOf();
    String spEntityId = getClientMetaData().getAppEntityId();
    if (PERSON_ID_SELF.equals(userId)) {
      userId = onBehalfOf;
    } else if (!userId.startsWith(LdapClient.URN_IDENTIFIER)) {
      // persistent identifier, need urn to query
      PersonEntry person = personService.getPerson(userId, onBehalfOf, spEntityId);
      userId = person.getEntry().getId();
    }
    LOG.info("Got getGroups-request, for userId '{}',  on behalf of '{}'", new Object[] { userId, onBehalfOf });

    List<GroupProvider> allGroupProviders = getAllAllowedGroupProviders(Service.Group);
    // sensible default
    Group20Entry group20Entry = new Group20Entry(new ArrayList<Group20>());

    GroupProvider grouper = getGrouperProvider(allGroupProviders);
    boolean grouperAllowed = groupProviderConfiguration.isCallAllowed(Service.Group, spEntityId, grouper);
    if (!grouperAllowed) {
      sendAclMissingMail(grouper, spEntityId, userId, Service.People);
    } else {
      group20Entry = groupService.getGroups20(userId, onBehalfOf, count, startIndex, sortBy);
      if (group20Entry != null && !group20Entry.getEntry().isEmpty()) {
        group20Entry = groupProviderConfiguration.addUrnPartForGrouper(allGroupProviders, group20Entry);
      }
    }

    List<Group20> listOfAllExternalGroups = new ArrayList<Group20>();
    // Now see which external groupProvider can also handle this call
    for (GroupProvider groupProvider : allGroupProviders) {
      /*
       * Do we need to make calls this external group provider?
       */
      if (groupProvider.isExternalGroupProvider() && groupProvider.isMeantForUser(userId)) {
        Group20Entry externalGroups = groupProviderConfiguration.getGroup20Entry(groupProvider, userId,
            count == null ? Integer.MAX_VALUE : count, startIndex == null ? 0 : startIndex);
        if (externalGroups != null) {
          List<Group20> groups = externalGroups.getEntry();
          if (groups != null) {
            listOfAllExternalGroups.addAll(groups);
          }
        }
      }
    }
    if (!listOfAllExternalGroups.isEmpty()) {
      group20Entry.getEntry().addAll(listOfAllExternalGroups);
      group20Entry = addLinkedSurfTeamGroupsForExternalGroups(userId, group20Entry, listOfAllExternalGroups, allGroupProviders);
    }

    logApiCall(onBehalfOf);
    setResultOptions(group20Entry, count, startIndex, sortBy);
    return group20Entry;
  }
  
  @RequestMapping(method = RequestMethod.GET, value = "/osgroups/{userId:.+}")
  @ResponseBody
  public GroupEntry getOsGroups(@PathVariable("userId")
  String userId, @RequestParam(value = "count", required = false)
  Integer count, @RequestParam(value = "startIndex", required = false)
  Integer startIndex, @RequestParam(value = "sortBy", required = false)
  String sortBy) {
    return new GroupEntry(getGroups(userId, count, startIndex, sortBy));
    
  }
 

  /*
   * Get all SURFTeams groups for those external groups that linked to one or
   * more SURFTeams. Add those teams to the final list to return.
   */
  @SuppressWarnings("unchecked")
  private Group20Entry getGrouperTeamsLinkedToExternalGroup(String userId, String externalGroupId) {
    // sensible default
    Group20Entry groups20Entry = new Group20Entry(new ArrayList<Group20>());
    List<TeamExternalGroup> linkedExternalGroups = teamExternalGroupDao.getByExternalGroupIdentifier(externalGroupId);
    if (!CollectionUtils.isEmpty(linkedExternalGroups)) {
      List<String> grouperTeamIds = new ArrayList<String>();
      for (TeamExternalGroup teamExternalGroup : linkedExternalGroups) {
        grouperTeamIds.add(teamExternalGroup.getGrouperTeamId());
      }
      groups20Entry = groupService.getGroups20ByIds(userId, grouperTeamIds.toArray(new String[grouperTeamIds.size()]),
          0, 0);
    }
    return groups20Entry;
  }

  /*
   * Get all SURFTeams groups for those external groups that linked to one or
   * more SURFTeams. Add those teams to the final list to return.
   */
  @SuppressWarnings("unchecked")
  private Group20Entry addLinkedSurfTeamGroupsForExternalGroups(String userId, Group20Entry group20Entry,
      List<Group20> listOfAllExternalGroups, List<GroupProvider> allGroupProviders) {
    List<String> grouperTeamIds = new ArrayList<String>();
    Collection<String> identifiers = CollectionUtils.collect(listOfAllExternalGroups, new Transformer() {
      @Override
      public Object transform(Object input) {
        return ((Group20) input).getId();
      }
    });
    List<TeamExternalGroup> externalGroupIdentifiers = teamExternalGroupDao.getByExternalGroupIdentifiers(identifiers);
    for (TeamExternalGroup teamExternalGroup : externalGroupIdentifiers) {
      grouperTeamIds.add(teamExternalGroup.getGrouperTeamId());
    }
    if (!grouperTeamIds.isEmpty()) {
      Group20Entry linkedTeams = groupService.getGroups20ByIds(userId,
          grouperTeamIds.toArray(new String[grouperTeamIds.size()]), 0, 0);
      linkedTeams = groupProviderConfiguration.addUrnPartForGrouper(allGroupProviders, linkedTeams);

      group20Entry.getEntry().addAll(linkedTeams.getEntry());
      // remove duplicates: convert to set and back.
      group20Entry.setEntry(new ArrayList<Group20>(new HashSet<Group20>(group20Entry.getEntry())));
    }
    return group20Entry;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/groups/{userId:.+}/{groupId}")
  @ResponseBody
  public Group20Entry getGroup(@PathVariable("userId")
  String userId, @PathVariable("groupId")
  String groupId) {
    invariant();
    String onBehalfOf = getOnBehalfOf();
    String spEntityId = getClientMetaData().getAppEntityId();
    if (PERSON_ID_SELF.equals(userId)) {
      userId = onBehalfOf;
    } else if (!userId.startsWith(LdapClient.URN_IDENTIFIER)) {
      // persistent identifier, need urn to query
      PersonEntry person = personService.getPerson(userId, userId, spEntityId);
      userId = person.getEntry().getId();
    }
    if (onBehalfOf == null) {
      onBehalfOf = userId;
    }
    List<GroupProvider> allGroupProviders = getAllAllowedGroupProviders(Service.Group);
    // sensible default
    Group20Entry group20Entry = new Group20Entry(new ArrayList<Group20>());

    GroupProvider grouper = getGrouperProvider(allGroupProviders);
    boolean grouperAllowed = groupProviderConfiguration.isCallAllowed(Service.Group, spEntityId, grouper);
    if (!grouperAllowed) {
      sendAclMissingMail(grouper, spEntityId, userId, Service.Group);
    }
    List<Group20> externalGroups = new ArrayList<Group20>();
    /*
     * Is the call to Grouper necessary (e.g. is this an internal group)?
     */
    if (grouperAllowed && groupProviderConfiguration.isInternalGroup(groupId)) {
      // need to cut off the urn part in order for Grouper
      String grouperGroupId = groupProviderConfiguration.cutOffUrnPartForGrouper(allGroupProviders, groupId);
      group20Entry = groupService.getGroup20(userId, grouperGroupId, onBehalfOf);
      if (group20Entry != null && group20Entry.getEntry() != null && group20Entry.getEntry().isEmpty()) {
        group20Entry = groupProviderConfiguration.addUrnPartForGrouper(allGroupProviders, group20Entry);
      }
    } else {
      // external group. see which groupProvider can handle this call
      for (GroupProvider groupProvider : allGroupProviders) {
        /*
         * Do we need to make calls to this external group provider?
         */
        if (groupProvider.isExternalGroupProvider() && groupProvider.isMeantForUser(onBehalfOf)) {
          Group20 group = groupProviderConfiguration.getGroup20(groupProvider, userId, groupId);
          if (group != null) {
            externalGroups.add(group);
          }
        }
      }
      if (!CollectionUtils.isEmpty(externalGroups)) {
        group20Entry.getEntry().addAll(externalGroups);
        group20Entry = addLinkedSurfTeamGroupsForExternalGroups(userId, group20Entry, externalGroups, allGroupProviders);

      }

    }
    logApiCall(onBehalfOf);
    setResultOptions(group20Entry, 0, 0, null);
    return group20Entry;
  }

  /**
   * Handler for RuntimeExceptions. It makes API return a model containing the original exception's message.
   * @param e the exception
   * @return the response body
   */
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  @ExceptionHandler(RuntimeException.class)
  @SuppressWarnings("unused")
  public Object handleException(RuntimeException e) {
    DateTime date = new DateTime();
    LOG.error("Handling error, will respond with the exception message. Current date: " +  date +
        ".", e);
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("error", "An internal server error occurred.");
    model.put("detail", e.getMessage());
    model.put("date", date.toString());
    return model;
  }

  /*
   * Set the metadata for the result
   */
  protected void setResultOptions(AbstractEntry entry, Integer count, Integer startIndex, String sortBy) {
    entry.setFiltered(false);
    entry.setItemsPerPage((count != null && count != 0) ? count : entry.getEntrySize());
    entry.setSorted(sortBy != null);
    entry.setStartIndex((startIndex != null && startIndex != 0) ? startIndex : 0);
    entry.setTotalResults(entry.getEntrySize());
    entry.setUpdatedSince(false);
  }

  protected void invariant() {
  }

  /*
   * Send a mail
   */
  protected void sendAclMissingMail(GroupProvider groupProvider, String spEntityId, String identifier, Service service) {
    String shortMessage = "Unauthorized attempt to api.surfconext";
    String formattedMessage = String.format(
        "Service Provider '%s' attempts to call '%s' on groupProvider '%s' for identifer '%s'", spEntityId, service,
        groupProvider, identifier);
    ErrorMail errorMail = new ErrorMail(shortMessage, formattedMessage, formattedMessage, getHost(), "API");
    errorMail.setLocation(this.getClass().getName() + "#get" + service);
    errorMessageMailer.sendErrorMail(errorMail);
  }

  protected List<GroupProvider> getAllAllowedGroupProviders(Service service) {
    List<GroupProvider> allGroupProviders = groupProviderConfiguration.getAllGroupProviders();
    String spEntityId = getClientMetaData().getAppEntityId();
    return groupProviderConfiguration.getAllowedGroupProviders(service, spEntityId, allGroupProviders);

  }

  private String getHost() {
    try {
      return InetAddress.getLocalHost().toString();
    } catch (UnknownHostException e) {
      return "UNKNOWN";
    }
  }

  /*
   * Save the fact that a request is made
   */
  protected void logApiCall(String onBehalfOf) {
    ApiCallLog log = ApiCallLogContextListener.getApiCallLog();
    ClientMetaData clientMetaData = getClientMetaData();
    log.setSpEntityId(clientMetaData.getAppEntityId());
    log.setConsumerKey(clientMetaData.getConsumerKey());
    log.setUserId(onBehalfOf);
    addApiCallLogInfo(log);
    logService.saveApiCallLog(log);
  }

  /**
   * Hook for subclasses to change the log record
   * 
   * @param log the log record
   */
  protected void addApiCallLogInfo(ApiCallLog log) {

    
  }

  private GroupProvider getGrouperProvider(List<GroupProvider> allGroupProviders) {
    for (GroupProvider groupProvider : allGroupProviders) {
      if (groupProvider.getGroupProviderType().equals(GroupProviderType.GROUPER)) {
        return groupProvider;
      }
    }
    return null;
  }

}
