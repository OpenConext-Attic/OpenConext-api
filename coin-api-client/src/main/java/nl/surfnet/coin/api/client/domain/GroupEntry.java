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

import static nl.surfnet.coin.api.client.domain.Group20Entry.NULL_SAFE_STRING_COMPARATOR;
import static nl.surfnet.coin.api.client.domain.Group20Entry.SORT_ATR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 *
 */
@SuppressWarnings({ "serial", "unchecked" })
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

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.api.client.domain.AbstractEntry#getEntrySize()
   */
  @Override
  public int getEntrySize() {
    return this.entry != null ? this.entry.size() : 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.api.client.domain.AbstractEntry#getEntryCollection()
   */
  @SuppressWarnings("rawtypes")
  @Override
  @JsonIgnore
  public List getEntryCollection() {
    return entry;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.domain.AbstractEntry#sortEntryCollection(java
   * .lang.String)
   */
  @Override
  @JsonIgnore
  public void sortEntryCollection(String sort) {
    if (StringUtils.isNotBlank(sort) && SORT_ATR.contains(sort)) {
      BeanComparator beanComparator = new BeanComparator(sort, NULL_SAFE_STRING_COMPARATOR);
      Collections.sort(entry, beanComparator);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.domain.AbstractEntry#setEntryCollection(java
   * .util.List)
   */
  @Override
  @JsonIgnore
  public void setEntryCollection(List entry) {
    this.entry = entry;
  }

}
