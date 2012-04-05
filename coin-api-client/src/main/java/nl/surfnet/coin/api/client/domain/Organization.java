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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 
 *
 */
@JsonIgnoreProperties(value = {"value"})
public class Organization {
  private String name;
  private String type;
  private String department;
  private String title;

  public Organization() {
    super();
  }

  public Organization(String name) {
    super();
    this.name = name;
  }

  public Organization(String name, String type, String department, String title) {
    super();
    this.name = name;
    this.type = type;
    this.department = department;
    this.title = title;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return name;
  }

  /**
   * @param value
   *          the value to set
   */
  public void setValue(String value) {
    this.name = value;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @return the department
   */
  public String getDepartment() {
    return department;
  }

  /**
   * @param department the department to set
   */
  public void setDepartment(String department) {
    this.department = department;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

}
