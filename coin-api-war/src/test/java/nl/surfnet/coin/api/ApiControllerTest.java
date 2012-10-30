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
package nl.surfnet.coin.api;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import nl.surfnet.coin.api.client.domain.AbstractEntry;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.domain.PersonEntry;

import org.junit.Test;

/**
 * ApiControllerTest.java
 * 
 */
public class ApiControllerTest {

  private ApiController controller = new ApiController();

  @Test
  public void testSetResultOptionsHappyFlowEntryGroup20() {
    AbstractEntry entry = getAbstractEntryGroup20(10);
    controller.setResultOptions(entry, 1, 5, "title");
    assertEquals(1, entry.getEntryCollection().size());
    assertEquals("title5", ((Group20) entry.getEntryCollection().get(0)).getTitle());

  }

  @Test
  public void testSetResultOptionsEqualCountIndexEntryGroup20() {
    AbstractEntry entry = getAbstractEntryGroup20(10);
    controller.setResultOptions(entry, 10, 10, "title");
    assertEquals(0, entry.getEntryCollection().size());
  }

  @Test
  public void testSetResultOptionsToHighStartIndexEntryGroup20() {
    AbstractEntry entry = getAbstractEntryGroup20(10);
    controller.setResultOptions(entry, null, 25, "title");
    assertEquals(0, entry.getEntryCollection().size());

  }

  @Test
  public void testSetResultOptionsToPreventResult0EntryGroup20() {
    AbstractEntry entry = getAbstractEntryGroup20(22);
    controller.setResultOptions(entry, 0, 20, "title");
    assertEquals(2, entry.getEntryCollection().size());

  }

  @Test
  public void testSetResultOptionsEmptyEntryGroup20() {
    AbstractEntry entry = getAbstractEntryGroup20(22);
    controller.setResultOptions(entry, 3, 20, "title");
    assertEquals(2, entry.getEntryCollection().size());

  }

  @Test
  public void testSetResultOptionsNotemptyResult0EntryGroup20() {
    AbstractEntry entry = getAbstractEntryGroup20(22);
    controller.setResultOptions(entry, 3, 15, "title");
    assertEquals(3, entry.getEntryCollection().size());

  }

  @Test
  public void testSetResultOptionsStartIndexEqualsCount() {
    AbstractEntry entry = getAbstractEntryGroup20(1);
    controller.setResultOptions(entry, 1, 1, "title");
    assertEquals(0, entry.getEntryCollection().size());

  }

  @Test
  public void testSetResultOptionsNullOptionsEntryGroup20() {
    AbstractEntry entry = getAbstractEntryGroup20(3);
    controller.setResultOptions(entry, null, null, "title");
    assertEquals(3, entry.getEntryCollection().size());
  }

  @Test
  public void testSetResultOptionsWithOnlyCountEntryGroup20() {
    AbstractEntry entry = getAbstractEntryGroup20(3);
    controller.setResultOptions(entry, 1, null, "title");
    assertEquals(1, entry.getEntryCollection().size());
  }

  @Test
  public void testSetResultOptions0OPtionsEntryGroup20() {
    AbstractEntry entry = getAbstractEntryGroup20(3);
    controller.setResultOptions(entry, 0, 0, "description");
    assertEquals(3, entry.getEntryCollection().size());

  }

  @Test
  public void testSetResultOptionsCornerCaseEntryGroup20() {
    AbstractEntry entry = getAbstractEntryGroup20(10);
    controller.setResultOptions(entry, 3, 10, "title");
    assertEquals(0, entry.getEntryCollection().size());

  }
  
  @Test
  public void testSetResultWithPersonEntry() {
    AbstractEntry entry = new PersonEntry(new Person(), 1, 1, "name", 5);
    controller.setResultOptions(entry, 3, 2, "name");
    assertEquals(1, entry.getTotalResults());
  }
  
  @Test
  public void testSetResultWithPersonEntryControllerBehaviour() {
    AbstractEntry entry = new PersonEntry(new Person(), 1, 1, "name", 5);
    controller.setResultOptions(entry, 3, 2, "name");
    assertEquals(1, entry.getTotalResults());
  }

  private AbstractEntry getAbstractEntryGroup20(int total) {
    List<Group20> groups = new ArrayList<Group20>();
    for (int i = total; i > 0; i--) {
      groups.add(new Group20("id" + i, "title" + i, "description" + i));
    }
    Group20Entry entry = new Group20Entry(groups);
    return entry;
  }

}
