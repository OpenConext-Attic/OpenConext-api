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

package nl.surfnet.coin.teams.util;

import java.util.List;

import nl.surfnet.coin.teams.domain.ConversionRule;
import nl.surfnet.coin.teams.domain.GroupProvider;

/**
 * Converts person and group identifiers.
 * <p/>
 * SURFconext uses "urn:collab:(group|person):myuniversity.nl:myId", the institutions use only "myId"
 */
public class GroupProviderPropertyConverter {

  public static final String PROPERTY_ID = "id";
  public static final String PROPERTY_NAME = "name";
  public static final String PROPERTY_DESCRIPTION = "description";


  /**
   * Converts a SURFconext person id (urn:collab:person:myuniversity.nl:myId) to external form the
   * group provider knows (myId)
   *
   *
   * @param input         person identifier used within the SURFconext platform
   * @param groupProvider {@link nl.surfnet.coin.teams.domain.GroupProvider}
   * @return person identifier used at the external group provider
   */
  public static String convertToExternalPersonId(String input, GroupProvider groupProvider) {
    final List<ConversionRule> converters = groupProvider.getPersonDecorators();

    return convertProperty(PROPERTY_ID, input, converters);
  }

  /**
   * Converts an external person id provided by the group provider (myId) into a person id used by the
   * SURFconext platform (urn:collab:person:myuniversity.nl:myId)
   *
   *
   * @param input         person identifier used by the group provider
   * @param groupProvider {@link nl.surfnet.coin.teams.domain.GroupProvider}
   * @return person identifier used within the SURFconext platform
   */
  public static String convertToSurfConextPersonId(String input, GroupProvider groupProvider) {
    final List<ConversionRule> converters = groupProvider.getPersonFilters();

    return convertProperty(PROPERTY_ID, input, converters);
  }

  /**
   * Converts a SURFconext group id (urn:collab:group:myuniversity.nl:myGroupId) to external form the
   * group provider knows (myGroupId)
   *
   *
   * @param input         group identifier used within the SURFconext platform
   * @param groupProvider {@link nl.surfnet.coin.teams.domain.GroupProvider}
   * @return group identifier used at the external group provider
   */
  public static String convertToExternalGroupId(String input, GroupProvider groupProvider) {
    final List<ConversionRule> converters = groupProvider.getGroupDecorators();

    return convertProperty(PROPERTY_ID, input, converters);
  }

  /**
   * Converts an external group id provided by the group provider (myGroupId) into a group id used by the
   * SURFconext platform (urn:collab:group:myuniversity.nl:myGroupId)
   *
   *
   * @param input         group identifier used by the group provider
   * @param groupProvider {@link nl.surfnet.coin.teams.domain.GroupProvider}
   * @return group identifier used within the SURFconext platform
   */
  public static String convertToSurfConextGroupId(String input, GroupProvider groupProvider) {
    final List<ConversionRule> converters = groupProvider.getGroupFilters();

    return convertProperty(PROPERTY_ID, input, converters);
  }

  /**
   * Converts input if there are conversion rules for the given property name
   *
   * @param propertyName  name of a property that will be converted
   * @param propertyValue value of a property
   * @param converters    list of {@link nl.surfnet.coin.teams.domain.ConversionRule}'s
   * @return converted String, can be the same as the input if no rule applies
   */
  public static String convertProperty(String propertyName, String propertyValue,
                                       List<ConversionRule> converters) {
    String s = propertyValue;
    for (ConversionRule converter : converters) {
      if (propertyName.equals(converter.getPropertyName())) {
        s = s.replaceAll(converter.getSearchPattern(), converter.getReplaceWith());
      }
    }
    return s;
  }
}
