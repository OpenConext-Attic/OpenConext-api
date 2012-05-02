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
package nl.surfnet.coin.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.ResultWrapper;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderType;
import nl.surfnet.coin.teams.service.GroupProviderService;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;

/**
 * @Test for {@link GroupProviderConfiguration}
 * 
 */
@Configuration
@ComponentScan(value = "nl.surfnet.coin.api", resourcePattern = "**/GroupProviderConfigurationImpl.class")
@ImportResource("classpath:test-cache-context.xml")
public class GroupProviderConfigurationTest {

  private GroupProviderConfigurationImpl configuration;

  private ObjectMapper objectMapper = new ObjectMapper().enable(
      DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY).setSerializationInclusion(
      JsonSerialize.Inclusion.NON_NULL);

  @Mock
  private GroupProviderService groupProviderService;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    configuration = new GroupProviderConfigurationImpl();
    configuration.setGroupProviderService(groupProviderService);
  }

  @Test
  public void testGrouperConfiguration() throws Exception{
    when(groupProviderService.getAllGroupProviders()).thenReturn(getGroupProviders());
  }

  /*
   * Set up test data
   */
  private List<GroupProvider> getGroupProviders() throws JsonParseException, JsonMappingException, IOException {
    List<GroupProvider> groupProviders = objectMapper.readValue(new ClassPathResource(
        "json/group-providers-configuration.json").getInputStream(), new TypeReference<List<GroupProvider>>() {
    });
    return groupProviders;
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.api.GroupProviderConfiguration#isExternalGroup(java.lang.String)}
   * .
   */
  @Test
  public void testIsExternalGroup() {

    assertFalse(configuration.isInternalGroup("urn:collab:group:hz.nl:bg001"));
    assertFalse(configuration.isInternalGroup("urn:collab:group:avans.nl:whatever"));
    assertFalse(configuration.isInternalGroup("urn:collab:group:hz.nl:surfteams"));

    assertFalse(configuration.isInternalGroup(""));
    assertFalse(configuration.isInternalGroup("x"));
    assertFalse(configuration.isInternalGroup(" "));

    assertTrue(configuration.isInternalGroup("urn:collab:group:surfteams.nl:nl:surfnet:diensten:videogadget"));
    assertTrue(configuration.isInternalGroup("urn:collab:group:surfteams.nl:nl:surfnet:diensten:[test_blockquate]"));
    assertTrue(configuration.isInternalGroup("urn:collab:group:test.surfteams.nl:nl:surfnet:diensten:managementvo"));
    assertTrue(configuration.isInternalGroup("urn:collab:group:dev.surfteams.nl:whatever"));

  }

  /**
   * Test to see if the cache works.
   * 
   */
  @Test
  public void testCache() throws JsonParseException, JsonMappingException, IOException {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(this.getClass());
    GroupProviderConfiguration configuration = (GroupProviderConfiguration) ctx.getBean("groupProviderConfiguration");
    GroupProviderService groupProviderService = (GroupProviderService) ctx.getBean("groupProviderService");

    when(groupProviderService.getAllGroupProviders()).thenReturn(getGroupProviders());
    configuration.getAllGroupProviders();

    when(groupProviderService.getAllGroupProviders()).thenThrow(new RuntimeException("Cache did not kick in"));
    /*
     * This is served from the cache
     */
    configuration.getAllGroupProviders();
  }

}
