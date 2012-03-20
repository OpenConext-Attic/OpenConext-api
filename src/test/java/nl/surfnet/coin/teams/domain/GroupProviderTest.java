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

package nl.surfnet.coin.teams.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Test class for {@link GroupProvider}
 */
public class GroupProviderTest {

  @Test
  public void emptyGroupProvider() {
    GroupProvider groupProvider = new GroupProvider(null, null, null, null);
    assertNull(groupProvider.getId());
    assertNull(groupProvider.getIdentifier());
    assertNull(groupProvider.getName());
    assertNull(groupProvider.getGroupProviderType());
  }

  @Test
  public void filledIn() {
    GroupProvider groupProvider = new GroupProvider(5L, "grouper", "SURFteams grouper",
        "EngineBlock_Group_Provider_Grouper");
    assertEquals(Long.valueOf(5L), groupProvider.getId());
    assertEquals("grouper", groupProvider.getIdentifier());
    assertEquals("SURFteams grouper", groupProvider.getName());
    assertEquals(GroupProviderType.GROUPER, groupProvider.getGroupProviderType());
  }
}
