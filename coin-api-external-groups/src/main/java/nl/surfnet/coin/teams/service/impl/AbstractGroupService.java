/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nl.surfnet.coin.teams.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.surfnet.coin.api.client.OpenConextJsonParser;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.teams.domain.GroupProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.PROPERTY_DESCRIPTION;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.PROPERTY_NAME;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertProperty;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertToSurfConextGroupId;
import static nl.surfnet.coin.teams.util.GroupProviderPropertyConverter.convertToSurfConextPersonId;

/**
 * Base class for {@link GroupServiceBasicAuthentication} and
 * {@link GroupServiceThreeLeggedOAuth10a}
 * 
 */
public abstract class AbstractGroupService {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractGroupService.class);

  private final static OpenConextJsonParser parser = new OpenConextJsonParser();
  
  protected final static Logger log = LoggerFactory.getLogger(AbstractGroupService.class);

  protected GroupMembersEntry getGroupMembersEntryFromResponse(InputStream in,
      GroupProvider provider) {
    try {
      return doGetGroupMembersEntryFromResponse(in, provider);
    /*
     * normally an antipattern, but we don't want to terminate the flow as we may have multiple GroupProviders 
     */
    } catch (Exception e) {
      return new GroupMembersEntry(new ArrayList<Person>());
    }
  }

  private GroupMembersEntry doGetGroupMembersEntryFromResponse(InputStream in,
      GroupProvider provider) {
    GroupMembersEntry groupMembersEntry = parser.parseTeamMembers(in);
    List<Person> persons = groupMembersEntry.getEntry();
    // iterator to prevent ConcurrentModificationException
    for (Iterator<Person> iterator = persons.iterator(); iterator.hasNext();) {
      Person person = iterator.next();
      String id = person.getId();
      if (StringUtils.hasText(id)) {
        String collabId = convertToSurfConextPersonId(id, provider);
        person.setId(collabId);
      } else {
        iterator.remove();
      }
    }
    return groupMembersEntry;
  }

  protected Group20Entry getGroup20EntryFromResponse(InputStream in,
      GroupProvider groupProvider) {
    try {
      return doGetGroup20EntryFromResponse(in, groupProvider);
    /*
     * normally an antipattern, but we don't want to terminate the flow as we may have multiple GroupProviders 
     */
    } catch (Exception e) {
      LOG.debug("Caught exception (msg: {}) while parsing response, will return empty group object", e.getMessage());
      return new Group20Entry(new ArrayList<Group20>());
    }
  }

 private Group20Entry doGetGroup20EntryFromResponse(InputStream in,
      GroupProvider groupProvider) {
    Group20Entry entry = parser.parseGroups20(in);
    for (Group20 group20 : entry.getEntry()) {
      String scGroupId = convertToSurfConextGroupId(group20.getId(),
          groupProvider);
      group20.setId(scGroupId);

      String scGroupName = convertProperty(PROPERTY_NAME, group20.getTitle(),
          groupProvider.getGroupFilters());
      group20.setTitle(scGroupName);

      String scGroupDesc = convertProperty(PROPERTY_DESCRIPTION,
          group20.getDescription(), groupProvider.getGroupFilters());

      group20.setDescription(scGroupDesc);
    }
    return entry;
  }

}
