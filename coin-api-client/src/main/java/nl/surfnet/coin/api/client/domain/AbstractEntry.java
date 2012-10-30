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

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 *
 */
@SuppressWarnings({"serial","rawtypes"})
public abstract class AbstractEntry implements Serializable {
  private int startIndex;
  private int totalResults;
  private int itemsPerPage;
  private boolean filtered;
  private boolean updatedSince;
  private boolean sorted;

  /**
   * @return the startIndex
   */
  public int getStartIndex() {
    return startIndex;
  }

  /**
   * @param startIndex
   *          the startIndex to set
   */
  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

  /**
   * @return the totalResults
   */
  public int getTotalResults() {
    return totalResults;
  }

  /**
   * @param totalResults
   *          the totalResults to set
   */
  public void setTotalResults(int totalResults) {
    this.totalResults = totalResults;
  }

  /**
   * @return the itemsPerPage
   */
  public int getItemsPerPage() {
    return itemsPerPage;
  }

  /**
   * @param itemsPerPage
   *          the itemsPerPage to set
   */
  public void setItemsPerPage(int itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
  }

  /**
   * @return boolean if the result set is filtered
   */
  public boolean isFiltered() {
    return filtered;
  }

  /**
   * @param filtered boolean if the result set is filtered
   */
  public void setFiltered(boolean filtered) {
    this.filtered = filtered;
  }

  /**
   * @return updated since
   */
  public boolean getUpdatedSince() {
    return updatedSince;
  }

  /**
   * @param updatedSince int to indicate when it was updated
   */
  public void setUpdatedSince(boolean updatedSince) {
    this.updatedSince = updatedSince;
  }

  /**
   * @return boolean to indicate if the result set is sorted
   */
  public boolean isSorted() {
    return sorted;
  }

  /**
   * @param sorted boolean to indicate if the result set is sorted
   */
  public void setSorted(boolean sorted) {
    this.sorted = sorted;
  }
  
  /**
   * 
   * @return the size of the embedded entry
   */
  @JsonIgnore
  public abstract int getEntrySize();
  
  @JsonIgnore
  public abstract List getEntryCollection();
  
  @JsonIgnore
  public abstract void setEntryCollection(List entry);

  @JsonIgnore
  public abstract void sortEntryCollection(String sort);
  
  
}
