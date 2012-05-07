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

package nl.surfnet.coin.api.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import nl.surfnet.coin.api.GroupProviderConfiguration;
import nl.surfnet.coin.api.GroupProviderConfiguration.Service;
import nl.surfnet.coin.api.client.OpenConextJsonParser;
import nl.surfnet.coin.api.client.domain.AbstractEntry;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupEntry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderType;
import nl.surfnet.coin.teams.domain.GroupProviderUserOauth;
import nl.surfnet.coin.teams.domain.ServiceProviderGroupAcl;
import nl.surfnet.coin.teams.service.GroupProviderService;
import nl.surfnet.coin.teams.util.GroupProviderPropertyConverter;

@Component(value = "mockService")
public class MockServiceImpl implements PersonService, GroupService, ConfigurableGroupProvider,
    GroupProviderConfiguration {

  private Logger LOG = LoggerFactory.getLogger(MockServiceImpl.class);

  private static final String GROUP_PROVIDERS_CONFIGURATION_JSON = "json/group-providers-configuration.json";

  private static boolean isActive;
  private static long sleepMilliseconds;

  private final static String JSON_PATH = "json/%s-%s.json";
  private final static String FALLBACK = "fallback";
  private OpenConextJsonParser parser = new OpenConextJsonParser();

  private static Map<String, Person> PERSONS_IN_MEMORY = new HashMap<String, Person>();
  private static Map<String, Group20> GROUPS_IN_MEMORY = new HashMap<String, Group20>();
  private static Map<String, List<String>> MEMBERSHIPS_IN_MEMORY = new HashMap<String, List<String>>();

  @Override
  public PersonEntry getPerson(String userId, String loggedInUser) {
    if (isActive) {
      return getPreparedPerson(userId);
    }
    /*
     * Strip all characters that might cause problems in filenames. e.g.:
     * urn:collab:person:test.surfguest.nl:foo becomes:
     * urn_collab_person_test.surfguest.nl_foo
     */
    String userIdStripped = userId.replaceAll("[^0-9a-zA-Z_.-]", "_");

    final String filename = String.format(JSON_PATH, userIdStripped, "person");
    LOG.debug("filename: {}", filename);
    ClassPathResource pathResource = new ClassPathResource(filename);
    if (!pathResource.exists()) {
      pathResource = new ClassPathResource(String.format(JSON_PATH, FALLBACK, "person"));
    }
    try {
      return parser.parsePerson(pathResource.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  private PersonEntry getPreparedPerson(String userId) {
    PersonEntry personEntry = new PersonEntry();
    Person person = PERSONS_IN_MEMORY.get(userId);
    if (person != null) {
      personEntry.setEntry(person);
      personEntry.setTotalResults(1);
    }
    return personEntry;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.service.PersonService#getGroupMembers(java.lang.String,
   * java.lang.String)
   */
  @Override
  public GroupMembersEntry getGroupMembers(String groupId, String onBehalfOf, Integer count, Integer startIndex,
      String sortBy) {
    if (isActive) {
      return getPreparedGroupMembers(groupId);
    }
    ClassPathResource pathResource = new ClassPathResource(String.format(JSON_PATH, groupId, "teammembers"));
    if (!pathResource.exists()) {
      pathResource = new ClassPathResource(String.format(JSON_PATH, FALLBACK, "teammembers"));
    }
    try {
      GroupMembersEntry entry = parser.parseTeamMembers(pathResource.getInputStream());
      processQueryOptions(entry, count, startIndex, sortBy, entry.getEntry());
      return entry;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private GroupMembersEntry getPreparedGroupMembers(String groupId) {
    GroupMembersEntry entry = new GroupMembersEntry();
    List<String> memberKeys = MEMBERSHIPS_IN_MEMORY.get(groupId);
    List<Person> members = new ArrayList<Person>();
    if (!CollectionUtils.isEmpty(memberKeys)) {
      for (String id : memberKeys) {
        members.add(PERSONS_IN_MEMORY.get(id));
      }
    }
    entry.setEntry(members);
    entry.setTotalResults(members.size());
    return entry;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.api.service.GroupService#getGroups(java.lang.String,
   * java.lang.String)
   */
  @Override
  public GroupEntry getGroups(String userId, String onBehalfOf, Integer count, Integer startIndex, String sortBy) {
    ClassPathResource pathResource = new ClassPathResource(String.format(JSON_PATH, userId, "groups"));
    if (!pathResource.exists()) {
      pathResource = new ClassPathResource(String.format(JSON_PATH, FALLBACK, "groups"));
    }
    try {
      return parser.parseGroups(pathResource.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Group20Entry getPreparedGroups20(String userId) {
    Group20Entry result = new Group20Entry();
    Set<Entry<String, List<String>>> entrySet = MEMBERSHIPS_IN_MEMORY.entrySet();
    List<Group20> groups20 = new ArrayList<Group20>();
    for (Entry<String, List<String>> entry : entrySet) {
      if (entry.getValue().contains(userId)) {
        groups20.add(GROUPS_IN_MEMORY.get(entry.getKey()));
      }
    }
    result.setTotalResults(groups20.size());
    result.setEntry(groups20);
    return result;

  }

  private Group20Entry getPreparedGroup20(String userId, String groupId) {
    Group20Entry result = new Group20Entry(new ArrayList<Group20>());
    Set<Entry<String, Group20>> entrySet = GROUPS_IN_MEMORY.entrySet();
    for (Entry<String, Group20> entry : entrySet) {
      if (entry.getValue().getId().equals(groupId)) {
        result.getEntry().add(entry.getValue());
        break;
      }
    }
    result.setTotalResults(result.getEntry().size());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.api.service.GroupService#getGroups20(java.lang.String,
   * java.lang.String)
   */
  @Override
  public Group20Entry getGroups20(String userId, String onBehalfOf, Integer count, Integer startIndex, String sortBy) {
    if (isActive) {
      return getPreparedGroups20(userId);
    }
    ClassPathResource pathResource = new ClassPathResource(String.format(JSON_PATH, userId, "groups20"));
    if (!pathResource.exists()) {
      pathResource = new ClassPathResource(String.format(JSON_PATH, FALLBACK, "groups20"));
    }
    try {
      return parser.parseGroups20(pathResource.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public GroupEntry getGroup(String userId, String groupId, String onBehalfOf) {
    throw new RuntimeException("GetGroup is not supported. Use GroupService#getGroup20");
  }

  @Override
  public Group20Entry getGroup20(String userId, String groupId, String onBehalfOf) {
    if (isActive) {
      return getPreparedGroup20(userId, groupId);
    }
    ClassPathResource pathResource = new ClassPathResource(String.format(JSON_PATH, groupId, "group20"));
    if (!pathResource.exists()) {
      pathResource = new ClassPathResource(String.format(JSON_PATH, FALLBACK, "group20"));
    }
    try {
      return parser.parseGroups20(pathResource.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isActive() {
    return isActive && sleep();
  }

  public void setActive(boolean isActive) {
    MockServiceImpl.isActive = isActive;
  }

  public void reset() {
    PERSONS_IN_MEMORY = new HashMap<String, Person>();
    GROUPS_IN_MEMORY = new HashMap<String, Group20>();
    MEMBERSHIPS_IN_MEMORY = new HashMap<String, List<String>>();
    isActive = false;
    sleepMilliseconds = 0;
  }

  private boolean sleep() {
    isActive = true;
    if (sleepMilliseconds != 0) {
      try {
        Thread.sleep(sleepMilliseconds);
      } catch (InterruptedException e) {
        throw new RuntimeException("InterruptedException in MockService#sleep", e);
      }
    }
    return true;
  }

  public void addPerson(Person person) {
    isActive = true;
    PERSONS_IN_MEMORY.put(person.getId(), person);

  }

  public void addGroup(Group20 group) {
    isActive = true;
    GROUPS_IN_MEMORY.put(group.getId(), group);

  }

  public void addPersonToGroup(String personId, String groupId) {
    isActive = true;
    Person person = PERSONS_IN_MEMORY.get(personId);
    Group20 group = GROUPS_IN_MEMORY.get(groupId);
    if (person == null || group == null) {
      // design decision
      return;
    }
    List<String> members = MEMBERSHIPS_IN_MEMORY.get(groupId);
    if (members == null) {
      members = new ArrayList<String>();
    }
    members.add(personId);
    MEMBERSHIPS_IN_MEMORY.put(groupId, members);
  }

  /**
   * Filter/mangle a result set based on query parameters
   * 
   * @param parent
   *          the root object; effectively this parameter is altered by setting
   *          the totalResults property
   * @param count
   *          nr of records to fetch
   * @param startIndex
   *          the start index
   * @param sortBy
   *          field to sort by
   * @param entry
   *          the result list of entries
   * @return
   */
  protected List<? extends Object> processQueryOptions(AbstractEntry parent, Integer count, Integer startIndex,
      String sortBy, List<? extends Object> entry) {
    parent.setTotalResults(entry.size());
    if (StringUtils.hasText(sortBy)) {
      BeanComparator comparator = new BeanComparator(sortBy);
      Collections.sort(entry, comparator);
      parent.setSorted(true);
    }
    if (startIndex != null) {
      entry = entry.subList(startIndex, entry.size());
      parent.setStartIndex(startIndex);
    }
    if (count != null) {
      entry = entry.subList(0, count);
      parent.setItemsPerPage(count);
    }

    return entry;
  }

  public void sleep(long millSeconds) {
    isActive = true;
    sleepMilliseconds = millSeconds;

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.GroupProviderConfiguration#isInternalGroup(java.lang
   * .String)
   */
  @Override
  public boolean isInternalGroup(String groupId) {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.GroupProviderConfiguration#getAllowedGroupProviders
   * (nl.surfnet.coin.api.GroupProviderConfiguration.Service, java.lang.String,
   * java.util.List)
   */
  @Override
  public List<GroupProvider> getAllowedGroupProviders(Service service, String spEntityId,
      List<GroupProvider> allGroupProviders) {
    return allGroupProviders;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.api.GroupProviderConfiguration#getAllGroupProviders()
   */
  @Override
  public List<GroupProvider> getAllGroupProviders() {
    try {
      List<GroupProvider> groupProviders = parser.getObjectMapper().readValue(
          new ClassPathResource(GROUP_PROVIDERS_CONFIGURATION_JSON).getInputStream(),
          new TypeReference<List<GroupProvider>>() {
          });
      return groupProviders;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.GroupProviderConfiguration#isGrouperCallsAllowed(nl
   * .surfnet.coin.api.GroupProviderConfiguration.Service, java.lang.String,
   * java.util.List)
   */
  @Override
  public boolean isCallAllowed(Service service, String spEntityId, GroupProvider groupProvider) {
    return !groupProvider.isExternalGroupProvider();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.GroupProviderConfiguration#getGroupMembersEntry(nl.
   * surfnet.coin.teams.domain.GroupProvider, java.lang.String,
   * java.lang.String, int, int)
   */
  @Override
  public GroupMembersEntry getGroupMembersEntry(GroupProvider groupProvider, String onBehalfOf, String groupId,
      int limit, int offset) {
    throw new RuntimeException(
        "This call is not supported (and should never be called as we don't support external group providers in mock modus)");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.GroupProviderConfiguration#getGroup20Entry(nl.surfnet
   * .coin.teams.domain.GroupProvider, java.lang.String, int, int)
   */
  @Override
  public Group20Entry getGroup20Entry(GroupProvider groupProvider, String userId, int limit, int offset) {
    throw new RuntimeException(
        "This call is not supported (and should never be called as we don't support external group providers in mock modus)");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.GroupProviderConfiguration#cutOffUrnPartForGrouper(
   * java.util.List, java.lang.String)
   */
  @Override
  public String cutOffUrnPartForGrouper(List<GroupProvider> groupProviders, String groupId) {
    for (GroupProvider groupProvider : groupProviders) {
      if (groupProvider.getGroupProviderType().equals(GroupProviderType.GROUPER)) {
        return GroupProviderPropertyConverter.convertToExternalGroupId(groupId, groupProvider);
      }
    }
    throw new RuntimeException("No Grouper groupProvider present in the list of groupProviders('" + groupProviders
        + "') so we can't cut off the groupId('" + groupId + "')");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.GroupProviderConfiguration#getGroup20(nl.surfnet.coin
   * .teams.domain.GroupProvider, java.lang.String, java.lang.String)
   */
  @Override
  public Group20 getGroup20(GroupProvider groupProvider, String userId, String groupId) {
    throw new RuntimeException(
        "This call is not supported (and should never be called as we don't support external group providers in mock modus)");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.GroupProviderConfiguration#addUrnPartForGrouper(java
   * .util.List, nl.surfnet.coin.api.client.domain.Group20Entry)
   */
  @Override
  public Group20Entry addUrnPartForGrouper(List<GroupProvider> groupProviders, Group20Entry group20Entry) {
    Group20Entry result = new Group20Entry(new ArrayList<Group20>());
    GroupProvider grouper = null;
    for (GroupProvider groupProvider : groupProviders) {
      if (groupProvider.getGroupProviderType().equals(GroupProviderType.GROUPER)) {
        grouper = groupProvider;
        break;
      }
    }
    if (grouper == null) {
      throw new RuntimeException("No Grouper groupProvider present in the list of groupProviders('" + groupProviders
          + "') so we can't add the surfconext urn part");
    }
    for (Group20 group : group20Entry.getEntry()) {
      String convertToSurfConextGroupId = GroupProviderPropertyConverter.convertToSurfConextGroupId(group.getId(),
          grouper);
      group.setId(convertToSurfConextGroupId);
      result.getEntry().add(group);
    }
    return result;
  }

}
