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
package nl.surfnet.coin.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderType;
import nl.surfnet.coin.teams.domain.GroupProviderUserOauth;
import nl.surfnet.coin.teams.domain.ServiceProviderGroupAcl;
import nl.surfnet.coin.teams.service.BasicAuthGroupService;
import nl.surfnet.coin.teams.service.GroupProviderService;
import nl.surfnet.coin.teams.service.OauthGroupService;
import nl.surfnet.coin.teams.util.GroupProviderPropertyConverter;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * The GroupProviderConfigurationImpl is thread-safe.
 * 
 */
@Component(value = "groupProviderConfiguration")
public class GroupProviderConfigurationImpl implements GroupProviderConfiguration {

  @Resource(name = "groupProviderService")
  private GroupProviderService groupProviderService;

  @Resource(name = "oauthGroupService")
  private OauthGroupService oauthGroupService;

  @Resource(name = "basicAuthGroupService")
  private BasicAuthGroupService basicAuthGroupService;

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.GroupProviderConfiguration#isInternalGroup(java.lang
   * .String)
   */
  @Override
  public boolean isInternalGroup(String groupId) {
    return INTERNAL_GROUP_PATTERN.matcher(groupId).matches();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.GroupProviderConfiguration#getAllowedGroupProviders
   * (nl.surfnet.coin.api.GroupProviderConfigurationImpl.Service,
   * java.lang.String)
   */
  @Override
  public List<GroupProvider> getAllowedGroupProviders(Service service, String spEntityId,
      List<GroupProvider> allGroupProviders) {
    List<GroupProvider> result = new ArrayList<GroupProvider>();
    // now see which groupProviders have the correct acl
    for (GroupProvider groupProvider : allGroupProviders) {
      if (isAclConfiguredForGroupProvider(groupProvider, spEntityId, service)) {
        result.add(groupProvider);
      }
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.api.GroupProviderConfiguration#getAllGroupProviders()
   */
  @Override
  @Cacheable(value = { "group-providers" }, key = "methodName")
  public List<GroupProvider> getAllGroupProviders() {
    return groupProviderService.getAllGroupProviders();
  }

  /*
   * Is there an Acl configured for the GroupProvider for the Service call
   */
  private boolean isAclConfiguredForGroupProvider(GroupProvider groupProvider, String spEntityId, Service service) {
    if (groupProvider == null) {
      return false;
    }
    List<ServiceProviderGroupAcl> acls = groupProvider.getServiceProviderGroupAcls();
    if (CollectionUtils.isEmpty(acls)) {
      return false;
    }
    for (ServiceProviderGroupAcl acl : acls) {
      if (acl.getSpEntityId().equals(spEntityId)) {
        switch (service) {
        case People:
          return acl.isAllowMembers();
        case Group:
          return acl.isAllowGroups();
        }
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.GroupProviderConfiguration#isGrouperCallsAllowed(java
   * .util.List)
   */
  @Override
  public boolean isCallAllowed(Service service, String spEntityId, GroupProvider groupProvider) {
    return isAclConfiguredForGroupProvider(groupProvider, spEntityId, service);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.GroupProviderConfiguration#getGroupMembersEntry(nl.
   * surfnet.coin.teams.domain.GroupProvider, java.lang.String, int, int)
   */
  @Override
  public GroupMembersEntry getGroupMembersEntry(GroupProvider groupProvider, String onBehalfOf, String groupId,
      int limit, int offset) {
    switch (groupProvider.getGroupProviderType()) {
    case BASIC_AUTHENTICATION: {
      return basicAuthGroupService.getGroupMembersEntry(groupProvider, onBehalfOf, groupId, limit, offset);
    }
    case OAUTH_THREELEGGED: {
      GroupProviderUserOauth oauth = groupProviderService.getGroupProviderUserOauth(onBehalfOf,
          groupProvider.getIdentifier());
      if (oauth != null) {
        return oauthGroupService.getGroupMembersEntry(oauth, groupProvider, groupId, limit, offset);
      }
      break;
    }
    default:
      throw new RuntimeException(String.format("Not supported GroupProviderType(%s)",
          groupProvider.getGroupProviderType()));
    }
    // sensible default
    return new GroupMembersEntry(new ArrayList<Person>());
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
    switch (groupProvider.getGroupProviderType()) {
    case BASIC_AUTHENTICATION: {
      return basicAuthGroupService.getGroup20Entry(groupProvider, userId, limit, offset);
    }
    case OAUTH_THREELEGGED: {
      GroupProviderUserOauth oauth = groupProviderService
          .getGroupProviderUserOauth(userId, groupProvider.getIdentifier());
      if (oauth != null) {
        return oauthGroupService.getGroup20Entry(oauth, groupProvider, limit, offset);
      }
      break;
    }
    default:
      throw new RuntimeException(String.format("Not supported GroupProviderType(%s)",
          groupProvider.getGroupProviderType()));
    }
    // sensible default
    return new Group20Entry(new ArrayList<Group20>());
  }

  @Override
  public Group20 getGroup20(GroupProvider groupProvider, String userId, String groupId) {
    switch (groupProvider.getGroupProviderType()) {
    case BASIC_AUTHENTICATION: {
      return basicAuthGroupService.getGroup20(groupProvider, userId, groupId);
    }
    case OAUTH_THREELEGGED: {
      GroupProviderUserOauth oauth = groupProviderService
          .getGroupProviderUserOauth(userId, groupProvider.getIdentifier());
      if (oauth != null) {
        return oauthGroupService.getGroup20(oauth, groupProvider, groupId);
      }
      break;
    }
    default:
      throw new RuntimeException(String.format("Not supported GroupProviderType(%s)",
          groupProvider.getGroupProviderType()));
    }
    // can't think of a sensible default
    return null;
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

  /**
   * @param groupProviderService
   *          the groupProviderService to set
   */
  public void setGroupProviderService(GroupProviderService groupProviderService) {
    this.groupProviderService = groupProviderService;
  }

  /**
   * @param oauthGroupService
   *          the oauthGroupService to set
   */
  public void setOauthGroupService(OauthGroupService oauthGroupService) {
    this.oauthGroupService = oauthGroupService;
  }

  /**
   * @param basicAuthGroupService the basicAuthGroupService to set
   */
  public void setBasicAuthGroupService(BasicAuthGroupService basicAuthGroupService) {
    this.basicAuthGroupService = basicAuthGroupService;
  }

}
