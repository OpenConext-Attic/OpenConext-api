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

import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderType;
import nl.surfnet.coin.teams.domain.GroupProviderUserOauth;
import nl.surfnet.coin.teams.domain.ServiceProviderGroupAcl;
import nl.surfnet.coin.teams.service.GroupProviderService;
import nl.surfnet.coin.teams.service.OauthGroupService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * The GroupProviderConfigurationImpl is thread-safe.
 * 
 */
@Component(value = "groupProviderConfiguration")
public class GroupProviderConfigurationImpl implements GroupProviderConfiguration {

  @Resource(name="groupProviderService")
  private GroupProviderService groupProviderService;

  @Resource(name="oauthGroupService")
  private OauthGroupService oauthGroupService;
  
  
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
  public List<GroupProvider> getAllowedGroupProviders(Service service, String spEntityId, List<GroupProvider> allGroupProviders) {
    List<GroupProvider> result = new ArrayList<GroupProvider>();
    // now see which groupProviders have the correct acl
    for (GroupProvider groupProvider : allGroupProviders) {
      if (isAclConfiguredForGroupProvider(groupProvider, spEntityId, service))
        result.add(groupProvider);
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
  public boolean isGrouperCallsAllowed(Service service, String spEntityId, List<GroupProvider> allGroupProviders) {
    for (GroupProvider groupProvider : allGroupProviders) {
      if (groupProvider.getGroupProviderType().equals(GroupProviderType.GROUPER)) {
        return isAclConfiguredForGroupProvider(groupProvider, spEntityId, service);
      }
    }
    return false;
  }

  /* (non-Javadoc)
   * @see nl.surfnet.coin.api.GroupProviderConfiguration#getGroupMembersEntry(nl.surfnet.coin.teams.domain.GroupProvider, java.lang.String, int, int)
   */
  @Override
  public GroupMembersEntry getGroupMembersEntry(GroupProvider groupProvider, String onBehalfOf, String groupId, int limit, int offset) {
    GroupProviderUserOauth oauth = groupProviderService.getGroupProviderUserOauth(onBehalfOf, groupProvider.getIdentifier());
    if (oauth != null) {
      return oauthGroupService.getGroupMembersEntry(oauth, groupProvider, groupId, limit, offset);
    }
    //sensible default
    return new GroupMembersEntry(new ArrayList<Person>());
  }

  /**
   * @param groupProviderService
   *          the groupProviderService to set
   */
  public void setGroupProviderService(GroupProviderService groupProviderService) {
    this.groupProviderService = groupProviderService;
  }

  /**
   * @param oauthGroupService the oauthGroupService to set
   */
  public void setOauthGroupService(OauthGroupService oauthGroupService) {
    this.oauthGroupService = oauthGroupService;
  }


}
