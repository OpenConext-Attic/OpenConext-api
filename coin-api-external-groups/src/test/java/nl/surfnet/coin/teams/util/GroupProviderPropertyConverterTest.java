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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.surfnet.coin.teams.domain.ConversionRule;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link GroupProviderPropertyConverter}
 */
public class GroupProviderPropertyConverterTest {

  private GroupProvider groupProvider;

  @Before
  public void setup() {
    groupProvider = new GroupProvider(1L, "external", "External Group Provider",
        GroupProviderType.OAUTH_THREELEGGED.getStringValue());
  }

  @Test
  public void testConvertToExternalPerson_noRule() {
    String personId = "user1";
    String externalPersonId = GroupProviderPropertyConverter.convertToExternalPersonId(personId, groupProvider);
    assertEquals(personId, externalPersonId);

  }

  @Test
  public void testConvertToExternalPerson_asIs() {
    String personId = "user1";

    final ConversionRule asIsConverter = getAsIsConverter();
    groupProvider.addPersonDecorator(asIsConverter);

    String externalPersonId = GroupProviderPropertyConverter.convertToExternalPersonId(personId, groupProvider);
    assertEquals(personId, externalPersonId);

  }

  @Test
  public void testConvertToExternalPerson_StripUrnCollab() {
    String personId = "urn:collab:person:myuniversity.nl:testuser";

    ConversionRule conversionRule = new ConversionRule();
    conversionRule.setPropertyName("id");
    conversionRule.setSearchPattern("urn:collab:person:myuniversity.nl:(.+)");
    conversionRule.setReplaceWith("$1");

    groupProvider.addPersonDecorator(conversionRule);

    String externalPersonId = GroupProviderPropertyConverter.convertToExternalPersonId(personId, groupProvider);
    assertEquals("testuser", externalPersonId);

  }
  
  @Test
  public void testIsGroupFromGroupProvider() {

    ConversionRule conversionRule = new ConversionRule();
    conversionRule.setPropertyName("id");
    conversionRule.setSearchPattern("urn:collab:group:myuniversity.nl:(.+)");
    conversionRule.setReplaceWith("$1");

    groupProvider.addGroupDecorator(conversionRule);

    boolean isGroup =  GroupProviderPropertyConverter.isGroupFromGroupProvider("urn:collab:group:myuniversity.nl:testgroup", groupProvider);
    assertTrue(isGroup);

    isGroup =  GroupProviderPropertyConverter.isGroupFromGroupProvider("urn:collab:group:differentyuniversity.nl:testgroup", groupProvider);
    assertFalse(isGroup);

    isGroup =  GroupProviderPropertyConverter.isGroupFromGroupProvider("urn:collab:group:surfteams.nl:testgroup", groupProvider);
    assertFalse(isGroup);

  }

  @Test
  public void testConvertToSurfConextPerson_noRule() {
    String personId = "user1";
    String externalPersonId = GroupProviderPropertyConverter.convertToSurfConextPersonId(personId, groupProvider);
    assertEquals(personId, externalPersonId);
  }

  @Test
  public void testConvertToSurfConextPerson_asIs() {
    String personId = "user1";

    final ConversionRule asIsConverter = getAsIsConverter();
    groupProvider.addPersonDecorator(asIsConverter);

    String externalPersonId = GroupProviderPropertyConverter.convertToSurfConextPersonId(personId, groupProvider);
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

    groupProvider.addPersonDecorator(conversionRule);

    String result = GroupProviderPropertyConverter.convertToExternalPersonId(externalPersonId, groupProvider);
    assertEquals(scPersonId, result);

  }

  /**/
  @Test
  public void testConvertToExternalGroup_noRule() {
    String identifier = "group1";
    String externalId = GroupProviderPropertyConverter.convertToExternalGroupId(identifier, groupProvider);
    assertEquals(identifier, externalId);

  }

  @Test
  public void testConvertToExternalGroup_asIs() {
    String identifier = "group1";

    final ConversionRule asIsConverter = getAsIsConverter();
    groupProvider.addGroupDecorator(asIsConverter);

    String externalId = GroupProviderPropertyConverter.convertToExternalGroupId(identifier, groupProvider);
    assertEquals(identifier, externalId);

  }

  @Test
  public void testConvertToExternalGroup_StripUrnCollab() {
    String identifier = "urn:collab:group:myuniversity.nl:testgroup";

    ConversionRule conversionRule = new ConversionRule();
    conversionRule.setPropertyName("id");
    conversionRule.setSearchPattern("urn:collab:group:myuniversity.nl:(.+)");
    conversionRule.setReplaceWith("$1");

    groupProvider.addGroupDecorator(conversionRule);

    String externalId = GroupProviderPropertyConverter.convertToExternalGroupId(identifier, groupProvider);
    assertEquals("testgroup", externalId);

  }

  @Test
  public void testConvertToSurfConextGroup_noRule() {
    String identifier = "group1";
    String externalId = GroupProviderPropertyConverter.convertToSurfConextGroupId(identifier, groupProvider);
    assertEquals(identifier, externalId);
  }

  @Test
  public void testConvertToSurfConextGroup_asIs() {
    String identifier = "group1";

    final ConversionRule asIsConverter = getAsIsConverter();
    groupProvider.addGroupDecorator(asIsConverter);

    String externalId = GroupProviderPropertyConverter.convertToSurfConextGroupId(identifier, groupProvider);
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

    groupProvider.addGroupDecorator(conversionRule);

    String result = GroupProviderPropertyConverter.convertToExternalGroupId(externalIdentifier, groupProvider);
    assertEquals(scIdentifier, result);
  }
  
  @Test
  public void testDummyProperty_nullvalue() {
    String propName = "foo";
    String propValue = null;
    
    List<ConversionRule> conversionRules = new ArrayList<ConversionRule>();
    assertNull(GroupProviderPropertyConverter.convertProperty(propName, propValue, conversionRules));
  }

  @Test
  public void testDummyProperty_noRule() {
    String propName = "foo";
    String propValue = "bar";

    List<ConversionRule> conversionRules = new ArrayList<ConversionRule>();
    String result = GroupProviderPropertyConverter.convertProperty(propName, propValue, conversionRules);

    assertEquals(propValue, result);
  }
  @Test
  public void testDummyProperty_asIs() {
    String propName = "foo";
    String propValue = "bar";

    List<ConversionRule> conversionRules = new ArrayList<ConversionRule>();
    final ConversionRule conversionRule = new ConversionRule();
    conversionRule.setPropertyName(propName);
    conversionRule.setSearchPattern("(.+)");
    conversionRule.setReplaceWith("$1");
    conversionRules.add(conversionRule);
    String result = GroupProviderPropertyConverter.convertProperty(propName, propValue, conversionRules);

    assertEquals(propValue, result);
  }
  @Test
  public void testDummyProperty_AddPrefix() {
    String propName = "foo";
    String propValue = "bar";

    List<ConversionRule> conversionRules = new ArrayList<ConversionRule>();
    final ConversionRule conversionRule = new ConversionRule();
    conversionRule.setPropertyName(propName);
    conversionRule.setSearchPattern("(.+)");
    conversionRule.setReplaceWith("foobar:$1");
    conversionRules.add(conversionRule);
    String result = GroupProviderPropertyConverter.convertProperty(propName, propValue, conversionRules);

    assertEquals("foobar:bar", result);
  }

  private ConversionRule getAsIsConverter() {
    ConversionRule conversionRule = new ConversionRule();
    conversionRule.setPropertyName("id");
    conversionRule.setSearchPattern("(.+)");
    conversionRule.setReplaceWith("$1");
    return conversionRule;
  }
}
