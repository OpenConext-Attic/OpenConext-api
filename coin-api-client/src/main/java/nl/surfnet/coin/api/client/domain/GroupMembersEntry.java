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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;

@SuppressWarnings("serial")
public class GroupMembersEntry extends AbstractEntry {

  private List<Person> entry;

  public GroupMembersEntry() {
    super();
  }

  /**
   * @param persons
   */
  public GroupMembersEntry(List<Person> persons) {
    super();
    entry = persons;
  }


  /**
   * @return the entry
   */
  public List<Person> getEntry() {
    return entry;
  }

  /**
   * @param entry the entry to set
   */
  public void setEntry(List<Person> entry) {
    this.entry = entry;
  }

  @JsonIgnore
  public boolean isEmpty() {
    return entry == null || entry.isEmpty();
  }

  /**
   * Returns whether the given personId is member of this group
   * @param personId the person
   * @return boolean
   */
  public boolean isMember(String personId)
  {
    boolean found = false;
    for (Person p : entry) {
      if (p.getId().equals(personId))
      {
        found = true;
        break;
      }
    }
    return found;
  }

  @Override
  public int getEntrySize() {
    return this.entry != null ? this.entry.size() : 0;
  }

  @SuppressWarnings("rawtypes")
  @Override
  @JsonIgnore
  public List getEntryCollection() {
    return entry;
  }

  @Override
  @JsonIgnore
  public void sortEntryCollection(String sort) {
    // no sorting on group members is supported
  }

  @Override
  @JsonIgnore
  public void setEntryCollection(List entry) {
    this.entry = entry;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .appendSuper(super.toString())
      .append(entry)
      .toString();
  }
}
