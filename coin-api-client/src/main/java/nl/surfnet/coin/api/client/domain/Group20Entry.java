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

import java.util.List;

/**
 * 
 *
 */
@SuppressWarnings("serial")
public class Group20Entry extends AbstractEntry {
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

  /* (non-Javadoc)
   * @see nl.surfnet.coin.api.client.domain.AbstractEntry#getEntrySize()
   */
  @Override
  public int getEntrySize() {
    return this.entry != null ? this.entry.size() : 0;
  }

 
}
