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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 */
@SuppressWarnings("serial")
public class GroupEntry extends AbstractEntry {
  private List<Group> entry;

  public GroupEntry() {
  }

  public GroupEntry(List<Group> groups) {
    this.entry = groups;
  }

  public GroupEntry(Group20Entry groups20) {
    List<Group> myEntry = new ArrayList<Group>();
    for (Group20 group20 : groups20.getEntry()) {
      myEntry.add(new Group(group20));
    }
    this.entry = myEntry;

    this.setFiltered(groups20.isFiltered());
    this.setItemsPerPage(groups20.getItemsPerPage());
    this.setSorted(groups20.isSorted());
    this.setStartIndex(groups20.getStartIndex());
    this.setTotalResults(groups20.getTotalResults());
    this.setUpdatedSince(groups20.getUpdatedSince());
  }

  /**
   * @return the entry
   */
  public List<Group> getEntry() {
    return entry;
  }

  /**
   * @param entry
   *          the entry to set
   */
  public void setEntry(List<Group> entry) {
    this.entry = entry;
  }

 
}
