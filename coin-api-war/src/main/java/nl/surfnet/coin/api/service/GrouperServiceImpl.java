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

import javax.annotation.Resource;

import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.ldap.LdapClient;
import nl.surfnet.coin.teams.service.impl.ApiGrouperDaoImpl;

public class GrouperServiceImpl implements GroupService {

  @Resource(name = "apiGrouperDao")
  private ApiGrouperDaoImpl apiGrouperDao;

  @Resource(name="ldapClient")
  private LdapClient ldapClient;

  @Override
  public GroupEntry getGroups(String userId, String onBehalfOf, Integer count, Integer startIndex, String sortBy) {
    final Group20Entry groups20 = getGroups20(userId, onBehalfOf, count, startIndex, sortBy);
    return new GroupEntry(groups20);
  }

  @Override
  public Group20Entry getGroups20(String userId, String onBehalfOf, Integer count, Integer startIndex, String sortBy) {
    String userIdToUse = userId;
    if (!userId.startsWith(LdapClient.URN_IDENTIFIER)) {
      final Person person = ldapClient.findPerson(userId);
      userIdToUse = person.getId();
    }
    return apiGrouperDao.findAllGroup20sByMember(userIdToUse, startIndex, count, sortBy);
  }

  @Override
  public GroupEntry getGroup(String userId, String groupId, String onBehalfOf) {
    return new GroupEntry(getGroup20(userId, groupId, onBehalfOf));
  }

  @Override
  public Group20Entry getGroup20(String userId, String groupId, String onBehalfOf) {
    String userIdToUse = userId;
    if (!userId.startsWith(LdapClient.URN_IDENTIFIER)) {
      final Person person = ldapClient.findPerson(userId);
      userIdToUse = person.getId();
    }
    return apiGrouperDao.findGroup20(userIdToUse, groupId);
  }
}
