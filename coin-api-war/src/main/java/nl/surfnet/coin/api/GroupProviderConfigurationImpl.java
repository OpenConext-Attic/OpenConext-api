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
import java.util.regex.Pattern;

import javax.annotation.Resource;

import nl.surfnet.coin.api.oauth.ClientMetaData;
import nl.surfnet.coin.api.oauth.ExtendedBaseClientDetails;
import nl.surfnet.coin.api.oauth.ExtendedBaseConsumerDetails;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderType;
import nl.surfnet.coin.teams.domain.ServiceProviderGroupAcl;
import nl.surfnet.coin.teams.service.GroupProviderService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * The GroupProviderConfigurationImpl is thread-safe.
 * 
 */
@Component(value = "groupProviderConfiguration")
public class GroupProviderConfigurationImpl implements GroupProviderConfiguration {

  @Resource
  private GroupProviderService groupProviderService;

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
  public List<GroupProvider> getAllowedGroupProviders(Service service, String spEntityId) {
    List<GroupProvider> groupProviders = getAllGroupProviders();
    List<GroupProvider> result = new ArrayList<GroupProvider>();
    // now see which groupProviders have the correct acl
    for (GroupProvider groupProvider : groupProviders) {
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
  public boolean isGrouperCallsAllowed(List<GroupProvider> groupProviders) {
    for (GroupProvider groupProvider : groupProviders) {
      if (groupProvider.getGroupProviderType().equals(GroupProviderType.GROUPER)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param groupProviderService
   *          the groupProviderService to set
   */
  public void setGroupProviderService(GroupProviderService groupProviderService) {
    this.groupProviderService = groupProviderService;
  }

}
