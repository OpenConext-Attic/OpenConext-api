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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import nl.surfnet.coin.teams.util.PHPRegexConverter;

/**
 * Domain object for a Group provider
 */
public class GroupProvider {

  private Long id;
  private String identifier;
  private String name;
  private GroupProviderType groupProviderType;
  private Map<String, Object> allowedOptions;
  private List<ConversionRule> groupDecorators = new ArrayList<ConversionRule>();
  private List<ConversionRule> groupFilters = new ArrayList<ConversionRule>();
  private List<ConversionRule> personDecorators = new ArrayList<ConversionRule>();
  private List<ConversionRule> personFilters = new ArrayList<ConversionRule>();
  private String userIdPrecondition;

  public GroupProvider(Long id, String identifier, String name, String groupProviderType) {
    this.id = id;
    this.identifier = identifier;
    this.name = name;
    this.groupProviderType = GroupProviderType.fromString(groupProviderType);
    this.allowedOptions = new HashMap<String, Object>();
  }

  /**
   * @return unique identifier of the group provider
   */
  public Long getId() {
    return id;
  }

  /**
   * @return human readable unique identifier of the group provider
   */
  public String getIdentifier() {
    return identifier;
  }

  /**
   * @return name of the group provider
   */
  public String getName() {
    return name;
  }

  /**
   * @return {@link GroupProviderType} type of group provider
   */
  public GroupProviderType getGroupProviderType() {
    return groupProviderType;
  }

  /**
   * @return a Map<String, Object> with possible configuration options for the Group provider
   */
  public Map<String, Object> getAllowedOptions() {
    return allowedOptions;
  }

  /**
   * Add a configuration option
   * @param key the key
   * @param value the value
   */
  public void addAllowedOption(String key, Object value) {
    allowedOptions.put(key, value);
  }
  
  /**
   * Sets a Map<String,Object> with possible configuration options for the Group provider.
   *
   * @param allowedOptions allowed options for a Group provider
   */
  public void setAllowedOptions(Map<String, Object> allowedOptions) {
    this.allowedOptions = allowedOptions;
  }

  /**
   * Convenience method for allowed options
   *
   * @param key of the allowed option
   * @return String value of an allowed option. Can be {@literal null}
   */
  public String getAllowedOptionAsString(String key) {
    return (String) this.allowedOptions.get(key);
  }

  /**
   * Regex pattern a user id must match in order to use this GroupProvider. If {@literal null}, then all users can use
   * this Group Provider.
   *
   * @return regex pattern or {@link null}
   */
  public String getUserIdPrecondition() {
    return userIdPrecondition;
  }

  /**
   * @param userIdPrecondition regex pattern the user id must match.
   */
  public void setUserIdPrecondition(String userIdPrecondition) {
    this.userIdPrecondition = PHPRegexConverter.convertPHPRegexPattern(userIdPrecondition);
  }

  /**
   * Gets (outgoing) conversion rules to convert an urn:collab:groups:nl.myuniversity:group1 into group1
   *
   * @return List of {@link ConversionRule}'s
   */
  public List<ConversionRule> getGroupDecorators() {
    return groupDecorators;
  }

  /**
   * Sets (outgoing) conversion rules to convert an urn:collab:groups:nl.myuniversity:group1 into group1
   *
   * @param groupDecorators List of {@link ConversionRule}'s
   */
  public void setGroupDecorators(List<ConversionRule> groupDecorators) {
    this.groupDecorators = groupDecorators;
  }

  /**
   * Adds a single (outgoing) group id conversion rule
   *
   * @param groupIdDecorator {@link ConversionRule}
   */
  public void addGroupDecorator(ConversionRule groupIdDecorator) {
    this.groupDecorators.add(groupIdDecorator);
  }

  /**
   * Gets (incoming) conversion rules to convert group1 into urn:collab:groups:nl.myuniversity:group1
   *
   * @return List of {@link ConversionRule}'s
   */
  public List<ConversionRule> getGroupFilters() {
    return groupFilters;
  }

  /**
   * Sets (incoming) conversion rules to convert group1 into urn:collab:groups:nl.myuniversity:group1
   *
   * @param groupFilters List of {@link ConversionRule}'s
   */
  public void setGroupFilters(List<ConversionRule> groupFilters) {
    this.groupFilters = groupFilters;
  }

  /**
   * Adds single (incoming) group id conversion rule
   *
   * @param groupIdFilter {@link ConversionRule}
   */
  public void addGroupFilter(ConversionRule groupIdFilter) {
    this.groupFilters.add(groupIdFilter);
  }

  /**
   * Gets (outgoing) conversion rules to convert an urn:collab:person:nl.myuniversity:s123456 into s123456
   *
   * @return List of {@link ConversionRule}'s
   */
  public List<ConversionRule> getPersonDecorators() {
    return personDecorators;
  }

  /**
   * Sets (outgoing) conversion rules to convert an urn:collab:person:nl.myuniversity:s123456 into s123456
   *
   * @param personDecorators List of {@link ConversionRule}'s
   */
  public void setPersonDecorators(List<ConversionRule> personDecorators) {
    this.personDecorators = personDecorators;
  }

  /**
   * Adds single (outgoing) person id conversion rule
   *
   * @param personIdDecorator {@link ConversionRule}
   */
  public void addPersonDecorator(ConversionRule personIdDecorator) {
    this.personDecorators.add(personIdDecorator);
  }

  /**
   * Gets (incoming) conversion rules to convert s123456 into urn:collab:person:nl.myuniversity:s123456
   *
   * @return List of {@link ConversionRule}'s
   */
  public List<ConversionRule> getPersonFilters() {
    return personFilters;
  }

  /**
   * Sets (incoming) conversion rules to convert s123456 into urn:collab:person:nl.myuniversity:s123456
   *
   * @param personFilters List of {@link ConversionRule}'s
   */
  public void setPersonFilters(List<ConversionRule> personFilters) {
    this.personFilters = personFilters;
  }

  /**
   * Adds single (incoming) person id filter
   *
   * @param personIdFilter {@link ConversionRule}
   */
  public void addPersonFilter(ConversionRule personIdFilter) {
    this.personFilters.add(personIdFilter);
  }

  public boolean isMeantForUser(String userId) {
    return (!StringUtils.hasText(this.userIdPrecondition)) || userId.matches(this.userIdPrecondition);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    GroupProvider that = (GroupProvider) o;

    if (id != null ? !id.equals(that.id) : that.id != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("GroupProvider");
    sb.append("{id=").append(id);
    sb.append(", identifier='").append(identifier).append('\'');
    sb.append(", name='").append(name).append('\'');
    sb.append(", userIdPrecondition").append(userIdPrecondition).append('\'');
    sb.append(", groupProviderType=").append(groupProviderType);
    sb.append('}');
    return sb.toString();
  }
}
