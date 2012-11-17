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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for {@link GroupProvider}
 */
public class GroupProviderTest {

  private static final String ALLOWED_OPTION_KEY = "foo";

  @Test
  public void emptyGroupProvider() {
    GroupProvider groupProvider = new GroupProvider(null, null, null, GroupProviderType.GROUPER);
    assertNull(groupProvider.getId());
    assertNull(groupProvider.getIdentifier());
    assertNull(groupProvider.getName());
    assertEquals(Collections.emptyMap(), groupProvider.getAllowedOptions());
    assertNull(groupProvider.getAllowedOptionAsString(ALLOWED_OPTION_KEY));
    assertNull(groupProvider.getUserIdPrecondition());
  }

  @Test
  public void filledIn() {
    GroupProvider groupProvider = populateGroupProvider();

    assertEquals(Long.valueOf(5L), groupProvider.getId());
    assertEquals("grouper", groupProvider.getIdentifier());
    assertEquals("SURFteams grouper", groupProvider.getName());
    assertEquals(GroupProviderType.GROUPER, groupProvider.getGroupProviderType());
    assertEquals("bar", groupProvider.getAllowedOptionAsString(ALLOWED_OPTION_KEY));
    assertNull(groupProvider.getUserIdPrecondition());

    groupProvider.setUserIdPrecondition("|(.+)|");
    assertEquals("(.+)", groupProvider.getUserIdPrecondition());
  }

  @Test
  public void meantForUser_nothingConfigured() {
    GroupProvider groupProvider = populateGroupProvider();
    groupProvider.setUserIdPrecondition(null);
    assertTrue(groupProvider.isMeantForUser("urn:collab:person:nl.surfguest:guestuser"));
  }

  @Test
  public void meantForUser_matchesPrecondition() {
    GroupProvider groupProvider = populateGroupProvider();
    groupProvider.setUserIdPrecondition("urn:collab:person:nl.surfguest:(.+)");
    assertTrue(groupProvider.isMeantForUser("urn:collab:person:nl.surfguest:guestuser"));
  }

  @Test(expected=IllegalArgumentException.class)
  public void meantForUser_nullPointerException() {
    GroupProvider groupProvider = populateGroupProvider();
    groupProvider.setUserIdPrecondition("urn:collab:person:nl.surfguest:(.+)");
    assertFalse(groupProvider.isMeantForUser(null));
  }

  @Test
  public void meantForUser_doesNotMatchPrecondition() {
    GroupProvider groupProvider = populateGroupProvider();
    groupProvider.setUserIdPrecondition("urn:collab:person:nl.surfnet:(.+)");
    assertFalse(groupProvider.isMeantForUser("urn:collab:person:nl.surfguest:guestuser"));
  }

  private GroupProvider populateGroupProvider() {
    Map<String, Object> allowedOptions = new HashMap<String, Object>();
    allowedOptions.put(ALLOWED_OPTION_KEY, "bar");

    GroupProvider groupProvider = new GroupProvider(5L, "grouper", "SURFteams grouper",
        "EngineBlock_Group_Provider_Grouper");
    groupProvider.setAllowedOptions(allowedOptions);
    return groupProvider;
  }
}
