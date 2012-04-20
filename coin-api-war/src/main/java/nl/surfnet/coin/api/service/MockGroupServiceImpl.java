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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupEntry;

public class MockGroupServiceImpl implements GroupService {

  private static AtomicInteger groupId = new AtomicInteger(1);

  @Override
  public GroupEntry getGroups(String userId, String onBehalfOf) {
    final Group20Entry groups20 = getGroups20(userId, onBehalfOf);
    return new GroupEntry(groups20);
  }

  @Override
  public Group20Entry getGroups20(String userId, String onBehalfOf) {
    return new Group20Entry(Arrays.asList(createGroup(), createGroup(), createGroup()));
  }

  protected static Group20 createGroup() {
    int thisGroupId = groupId.getAndIncrement();
    return new Group20("groupId-" + thisGroupId, "group title", "description for group " + thisGroupId, "admin");
  }
}
