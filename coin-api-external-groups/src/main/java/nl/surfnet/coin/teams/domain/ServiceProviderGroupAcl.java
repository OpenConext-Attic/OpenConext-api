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
package nl.surfnet.coin.teams.domain;

import java.io.Serializable;

/**
 * 
 *
 */
public class ServiceProviderGroupAcl implements Serializable{

  private static final long serialVersionUID = 1L;
  private boolean allowGroups;
  private boolean allowMembers;
  private String spEntityId;
  private long groupProviderId;

  public ServiceProviderGroupAcl() {
    super();
  }

  public ServiceProviderGroupAcl(boolean allowGroups, boolean allowMembers,
      String spEntityId, long groupProviderId) {
    super();
    this.allowGroups = allowGroups;
    this.allowMembers = allowMembers;
    this.spEntityId = spEntityId;
    this.groupProviderId = groupProviderId;
  }

  /**
   * @return the allowGroups
   */
  public boolean isAllowGroups() {
    return allowGroups;
  }

  /**
   * @return the allowMembers
   */
  public boolean isAllowMembers() {
    return allowMembers;
  }

  /**
   * @return the spEntityId
   */
  public String getSpEntityId() {
    return spEntityId;
  }

  /**
   * @return the groupProviderId
   */
  public long getGroupProviderId() {
    return groupProviderId;
  }
}
