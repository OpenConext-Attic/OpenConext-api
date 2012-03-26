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

package nl.surfnet.coin.teams.domain;

import nl.surfnet.coin.teams.util.PHPRegexConverter;

/**
 * Search/replace of identifiers for person or group id's.
 * <p/>
 * Institutions provide the person id "s123456", for SURFconext we need to convert that into
 * urn:collab:person:nl.myuniversity:s123456 (and the other way around).
 */
public class IdConverter {

  private String propertyName;
  private String searchPattern;
  private String replaceWith;

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public String getSearchPattern() {
    return searchPattern;
  }

  public void setSearchPattern(String searchPattern) {
    this.searchPattern = PHPRegexConverter.convertPHPRegexPattern(searchPattern);
  }

  public String getReplaceWith() {
    return replaceWith;
  }

  public void setReplaceWith(String replaceWith) {
    this.replaceWith = replaceWith;
  }
}
