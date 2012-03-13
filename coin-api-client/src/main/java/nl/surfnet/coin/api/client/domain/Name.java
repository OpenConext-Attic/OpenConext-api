/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nl.surfnet.coin.api.client.domain;

/**
 * 
 *
 */
public class Name {
  private String formatted;
  private String familyName;
  private String givenName;

  /**
   * @return the formatted
   */
  public String getFormatted() {
    return formatted;
  }

  /**
   * @param formatted
   *          the formatted to set
   */
  public void setFormatted(String formatted) {
    this.formatted = formatted;
  }

  /**
   * @return the familyName
   */
  public String getFamilyName() {
    return familyName;
  }

  /**
   * @param familyName
   *          the familyName to set
   */
  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  /**
   * @return the givenName
   */
  public String getGivenName() {
    return givenName;
  }

  /**
   * @param givenName
   *          the givenName to set
   */
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }
}
