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
public class GroupProviderIdConverter {

  private static final String PROPERTY_ID = "id";


  /**
   * Converts a SURFconext person id (urn:collab:person:myuniversity.nl:myId) to external form the
   * group provider knows (myId)
   *
   * @param groupProvider {@link GroupProvider}
   * @param input         person identifier used within the SURFconext platform
   * @return person identifier used at the external group provider
   */
  public static String convertToExternalPersonId(GroupProvider groupProvider, String input) {
    final List<ConversionRule> converters = groupProvider.getPersonIdDecorators();

    return convertProperty(input, PROPERTY_ID, converters);
  }

  /**
   * Converts an external person id provided by the group provider (myId) into a person id used by the
   * SURFconext platform (urn:collab:person:myuniversity.nl:myId)
   *
   * @param groupProvider {@link GroupProvider}
   * @param input         person identifier used by the group provider
   * @return person identifier used within the SURFconext platform
   */
  public static String convertToSurfConextPersonId(GroupProvider groupProvider, String input) {
    final List<ConversionRule> converters = groupProvider.getPersonIdFilters();

    return convertProperty(input, PROPERTY_ID, converters);
  }

  /**
   * Converts a SURFconext group id (urn:collab:group:myuniversity.nl:myGroupId) to external form the
   * group provider knows (myGroupId)
   *
   * @param groupProvider {@link GroupProvider}
   * @param input         group identifier used within the SURFconext platform
   * @return group identifier used at the external group provider
   */
  public static String convertToExternalGroupId(GroupProvider groupProvider, String input) {
    final List<ConversionRule> converters = groupProvider.getGroupIdDecorators();

    return convertProperty(input, PROPERTY_ID, converters);
  }

  /**
   * Converts an external group id provided by the group provider (myGroupId) into a group id used by the
   * SURFconext platform (urn:collab:group:myuniversity.nl:myGroupId)
   *
   * @param groupProvider {@link GroupProvider}
   * @param input         group identifier used by the group provider
   * @return group identifier used within the SURFconext platform
   */
  public static String convertToSurfConextGroupId(GroupProvider groupProvider, String input) {
    final List<ConversionRule> converters = groupProvider.getGroupIdFilters();

    return convertProperty(input, PROPERTY_ID, converters);
  }

  /**
   * Converts input if there are conversion rules for the given property name
   *
   * @param input        String to modify
   * @param propertyName String that represents a property as in {@link ConversionRule#getPropertyName()}
   * @param converters   list of {@link ConversionRule}'s
   * @return converted String, can be the same as the input if no rule applies
   */
  private static String convertProperty(String input, String propertyName, List<ConversionRule> converters) {
    String s = input;
    for (ConversionRule converter : converters) {
      if (propertyName.equals(converter.getPropertyName())) {
        s = s.replaceAll(converter.getSearchPattern(), converter.getReplaceWith());
      }
    }
    return s;
  }
}
