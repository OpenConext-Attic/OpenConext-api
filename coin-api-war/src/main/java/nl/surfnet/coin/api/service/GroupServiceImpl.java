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

import org.springframework.stereotype.Component;

import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupEntry;
import nl.surfnet.coin.teams.service.impl.ApiGrouperDaoImpl;

@Component(value = "groupService")
public class GroupServiceImpl implements GroupService {

  @Resource(name = "apiGrouperDao")
  private ApiGrouperDaoImpl apiGrouperDao;

  @Override
  // FIXME: implement paging
  public GroupEntry getGroups(String userId, String onBehalfOf) {
    final Group20Entry groups20 = getGroups20(userId, onBehalfOf);
    return new GroupEntry(groups20);
  }

  @Override
  // FIXME: paging
  public Group20Entry getGroups20(String userId, String onBehalfOf) {
    final Group20Entry groups = apiGrouperDao.findAllGroup20sByMember(userId, 0, 0);
    // FIXME: restrict based on onBehalfOf
    return groups;
  }
}
