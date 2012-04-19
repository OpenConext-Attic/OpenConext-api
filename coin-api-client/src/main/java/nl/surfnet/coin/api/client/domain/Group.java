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

/**
 * Group representation
 * 
 */
public class Group {
  private GroupId id;
  private String title;
  private String description;
  private String voot_membership_role;

  public Group(GroupId id, String title, String description, String voot_membership_role) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.voot_membership_role = voot_membership_role;
  }
  public Group() {
  }

  /**
   * Some sort of copy constructor.
   * @param group
   */
  public Group(Group20 group) {
    this.id = new GroupId(group.getId(), GroupId.Type.groupId);
    this.title = group.getTitle();
    this.description = group.getDescription();
    this.voot_membership_role = group.getVoot_membership_role();
  }


  /**
   * @return the id
   */
  public GroupId getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(GroupId id) {
    this.id = id;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title
   *          the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the voot_membership_role
   */
  public String getVoot_membership_role() {
    return voot_membership_role;
  }

  /**
   * @param voot_membership_role
   *          the voot_membership_role to set
   */
  public void setVoot_membership_role(String voot_membership_role) {
    this.voot_membership_role = voot_membership_role;
  }
}
