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

import org.junit.Before;
import org.junit.Test;

import nl.surfnet.coin.teams.domain.ConversionRule;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderType;

import static junit.framework.Assert.assertEquals;

/**
 * Test class for {@link GroupProviderIdConverter}
 */
public class GroupProviderIdConverterTest {

  private GroupProvider groupProvider;

  @Before
  public void setup() {
    groupProvider = new GroupProvider(1L, "external", "External Group Provider",
        GroupProviderType.OAUTH_THREELEGGED.getStringValue());
  }

  @Test
  public void testConvertToExternalPerson_noRule() {
    String personId = "user1";
    String externalPersonId = GroupProviderIdConverter.convertToExternalPersonId(groupProvider, personId);
    assertEquals(personId, externalPersonId);

  }

  @Test
  public void testConvertToExternalPerson_asIs() {
    String personId = "user1";

    final ConversionRule asIsConverter = getAsIsConverter();
    groupProvider.addPersonIdDecorator(asIsConverter);

    String externalPersonId = GroupProviderIdConverter.convertToExternalPersonId(groupProvider, personId);
    assertEquals(personId, externalPersonId);

  }

  @Test
  public void testConvertToExternalPerson_StripUrnCollab() {
    String personId = "urn:collab:person:myuniversity.nl:testuser";

    ConversionRule conversionRule = new ConversionRule();
    conversionRule.setPropertyName("id");
    conversionRule.setSearchPattern("urn:collab:person:myuniversity.nl:(.+)");
    conversionRule.setReplaceWith("$1");

    groupProvider.addPersonIdDecorator(conversionRule);

    String externalPersonId = GroupProviderIdConverter.convertToExternalPersonId(groupProvider, personId);
    assertEquals("testuser", externalPersonId);

  }

  @Test
  public void testConvertToSurfConextPerson_noRule() {
    String personId = "user1";
    String externalPersonId = GroupProviderIdConverter.convertToSurfConextPersonId(groupProvider, personId);
    assertEquals(personId, externalPersonId);
  }

  @Test
  public void testConvertToSurfConextPerson_asIs() {
    String personId = "user1";

    final ConversionRule asIsConverter = getAsIsConverter();
    groupProvider.addPersonIdDecorator(asIsConverter);

    String externalPersonId = GroupProviderIdConverter.convertToSurfConextPersonId(groupProvider, personId);
    assertEquals(personId, externalPersonId);

  }

  @Test
  public void testConvertToSurfConextPerson_AddUrnCollab() {
    String externalPersonId = "testuser";
    String scPersonId = "urn:collab:person:myuniversity.nl:testuser";

    ConversionRule conversionRule = new ConversionRule();
    conversionRule.setPropertyName("id");
    conversionRule.setSearchPattern("(.+)");
    conversionRule.setReplaceWith("urn:collab:person:myuniversity.nl:$1");

    groupProvider.addPersonIdDecorator(conversionRule);

    String result = GroupProviderIdConverter.convertToExternalPersonId(groupProvider, externalPersonId);
    assertEquals(scPersonId, result);

  }
/**/
@Test
public void testConvertToExternalGroup_noRule() {
  String identifier = "group1";
  String externalId = GroupProviderIdConverter.convertToExternalGroupId(groupProvider, identifier);
  assertEquals(identifier, externalId);

}

@Test
public void testConvertToExternalGroup_asIs() {
  String identifier = "group1";

  final ConversionRule asIsConverter = getAsIsConverter();
  groupProvider.addGroupIdDecorator(asIsConverter);

  String externalId = GroupProviderIdConverter.convertToExternalGroupId(groupProvider, identifier);
  assertEquals(identifier, externalId);

}

@Test
public void testConvertToExternalGroup_StripUrnCollab() {
  String identifier = "urn:collab:group:myuniversity.nl:testgroup";

  ConversionRule conversionRule = new ConversionRule();
  conversionRule.setPropertyName("id");
  conversionRule.setSearchPattern("urn:collab:group:myuniversity.nl:(.+)");
  conversionRule.setReplaceWith("$1");

  groupProvider.addGroupIdDecorator(conversionRule);

  String externalId = GroupProviderIdConverter.convertToExternalGroupId(groupProvider, identifier);
  assertEquals("testgroup", externalId);

}

@Test
public void testConvertToSurfConextGroup_noRule() {
  String identifier = "group1";
  String externalId = GroupProviderIdConverter.convertToSurfConextGroupId(groupProvider, identifier);
  assertEquals(identifier, externalId);
}

@Test
public void testConvertToSurfConextGroup_asIs() {
  String identifier = "group1";

  final ConversionRule asIsConverter = getAsIsConverter();
  groupProvider.addGroupIdDecorator(asIsConverter);

  String externalId = GroupProviderIdConverter.convertToSurfConextGroupId(groupProvider, identifier);
  assertEquals(identifier, externalId);

}

@Test
public void testConvertToSurfConextGroup_AddUrnCollab() {
  String externalIdentifier = "testgroup";
  String scIdentifier = "urn:collab:group:myuniversity.nl:testgroup";

  ConversionRule conversionRule = new ConversionRule();
  conversionRule.setPropertyName("id");
  conversionRule.setSearchPattern("(.+)");
  conversionRule.setReplaceWith("urn:collab:group:myuniversity.nl:$1");

  groupProvider.addGroupIdDecorator(conversionRule);

  String result = GroupProviderIdConverter.convertToExternalGroupId(groupProvider, externalIdentifier);
  assertEquals(scIdentifier, result);

}

  private ConversionRule getAsIsConverter() {
    ConversionRule conversionRule = new ConversionRule();
    conversionRule.setPropertyName("id");
    conversionRule.setSearchPattern("(.+)");
    conversionRule.setReplaceWith("$1");
    return conversionRule;
  }
}
