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
public class GroupMembersEntry extends AbstractEntry {

  private List<Person> entry;

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

  
  public boolean isEmpty() {
    return entry != null && !entry.isEmpty();
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
  
}
