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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 *
 */
@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class Group20Entry extends AbstractEntry {

  @JsonIgnore
  public static List<String> SORT_ATR = Arrays.asList(new String[] { "id", "title", "description" });

  @JsonIgnore
  public static Comparator NULL_SAFE_STRING_COMPARATOR = new Comparator<String>() {
    @Override
    public int compare(String o1, String o2) {
      return (o1 == null ? "" : o1).compareTo(o2 == null ? "" : o2);
    }
  };

  private List<Group20> entry;

  public Group20Entry(List<Group20> groups) {
    this.entry = groups;
  }

  public Group20Entry() {
  }

  public Group20Entry(List<Group20> groups, int pageSize, int offset, String sortBy, int rowCount) {
    this.entry = groups;
    setFiltered(true);
    setItemsPerPage(pageSize);
    setSorted(true);
    setStartIndex(offset);
    setUpdatedSince(false);
    setTotalResults(rowCount);
  }

  /**
   * @return the entry
   */
  public List<Group20> getEntry() {
    return entry;
  }

  /**
   * @param entry
   *          the entry to set
   */
  public void setEntry(List<Group20> entry) {
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
