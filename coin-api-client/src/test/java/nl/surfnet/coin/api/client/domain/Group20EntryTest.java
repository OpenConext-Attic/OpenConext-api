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
package nl.surfnet.coin.api.client.domain;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Group20EntryTest.java
 * 
 */
public class Group20EntryTest {

  /**
   * Test method for
   * {@link nl.surfnet.coin.api.client.domain.Group20Entry#sortEntryCollection(java.lang.String)}
   * .
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSortEntryCollection() {
    Group20Entry entry = new Group20Entry(Arrays.asList(new Group20("id2", "c", "description1"), new Group20("id1", "B", null),
        new Group20("id1", "a", null)));
    entry.sortEntryCollection(null);
    assertEquals("id2", ((Group20) entry.getEntryCollection().get(0)).getId());

    entry.sortEntryCollection("id");
    assertEquals("id1", ((Group20) entry.getEntryCollection().get(0)).getId());

    entry.sortEntryCollection("description");
    assertNull(((Group20) entry.getEntryCollection().get(0)).getDescription());

    entry.sortEntryCollection("title");

    List<Group20> groups = entry.getEntryCollection();
    assertEquals("a", groups.get(0).getTitle());
    assertEquals("B", groups.get(1).getTitle());
    assertEquals("c", groups.get(2).getTitle());
  }

}
