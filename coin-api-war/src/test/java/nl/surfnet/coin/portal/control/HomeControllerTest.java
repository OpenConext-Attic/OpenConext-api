/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
* 
* http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/


package nl.surfnet.coin.portal.control;

import junit.framework.Assert;
import nl.surfnet.coin.opensocial.service.GroupService;
import nl.surfnet.coin.portal.domain.Gadget;
import nl.surfnet.coin.portal.domain.GadgetDefinition;
import nl.surfnet.coin.portal.domain.Tab;
import nl.surfnet.coin.portal.domain.UserPreferences;
import nl.surfnet.coin.portal.interceptor.LoginInterceptor;
import nl.surfnet.coin.portal.service.GadgetDefinitionService;
import nl.surfnet.coin.portal.service.InviteService;
import nl.surfnet.coin.portal.service.MetadataProvider;
import nl.surfnet.coin.portal.service.TabService;
import nl.surfnet.coin.portal.service.UserPreferencesService;
import nl.surfnet.coin.portal.util.CoinEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.Returns;
import org.opensocial.models.Person;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for the {@link HomeController}
 */
public class HomeControllerTest extends AbstractControllerTest {

  private HomeController homeController;

  @Before
  public void setup() throws Exception {
    super.setup();
    homeController = new HomeController();
    CoinEnvironment env = new CoinEnvironment();
    env.setContainerName("default");
    homeController.setCoinEnvironment(env);
  }

  /**
   * Test the metadata
   */
  @Test
  public void testStart() throws Exception {
    MockHttpServletRequest request = getRequest();
    // this is the start tab
    request.setParameter("tab", "1");

    UserPreferences preferences = new UserPreferences();
    UserPreferencesService userPreferencesService = mock(UserPreferencesService.class);
    autoWireMock(homeController, userPreferencesService, UserPreferencesService.class);

    TabService tabService = mock(TabService.class);
    Person person = (Person) request.getSession().getAttribute(
        LoginInterceptor.PERSON_SESSION_KEY);
    Tab tab = new Tab();
    tab.setId(1L);
    tab.setOwner(person.getId());

    Gadget gadget = new Gadget();
    gadget.setColumn(1);
    gadget.setDefinition(new GadgetDefinition());

    tab.addGadget(gadget);
    List<Tab> tabs = Collections.singletonList(tab);
    when(tabService.findFavorites(person.getId(), true)).thenReturn(tabs);
    when(tabService.findByIdAndOwner(1L, person.getId())).thenReturn(tab);

    autoWireMock(homeController, tabService, TabService.class);
    autoWireMock(homeController, new Returns(1), InviteService.class);
    autoWireMock(homeController, new Returns("meta-data-content"), MetadataProvider.class);
    autoWireMock(homeController, getGroupReturn(), GroupService.class);
    autoWireMock(homeController, new Returns(null), GadgetDefinitionService.class);

    homeController.start(getModelMap(), request);
    String metadata = (String) getModelMap().get("metadata");

    Assert.assertEquals("meta-data-content", metadata);
  }

  @Test
  public void testSecurityTokens() throws Exception {
    List<Gadget> gadgets = new ArrayList<Gadget>();
    Gadget gadget = new Gadget();
    GadgetDefinition def = new GadgetDefinition();
    def.setUrl("http://example.com/gadget.xml");
    gadget.setDefinition(def);
    gadget.setTab(new Tab());
    gadgets.add(gadget);

    String tokens = homeController.getSecurityTokens(gadgets, "john.doe");

    Assert.assertTrue(tokens.startsWith("['default%3A"));
    Assert.assertEquals(169, tokens.length());
  }

  @Test
  public void testGroupAwareness_empty() throws Exception {
    List<Gadget> gadgets = new ArrayList<Gadget>();

    String groupAwareness = homeController.getGroupAwareness(gadgets);

    Assert.assertEquals("[]", groupAwareness);
  }

  @Test
  public void testGroupAwareness_one() throws Exception {
    List<Gadget> gadgets = new ArrayList<Gadget>();
    Gadget gadget = new Gadget();
    GadgetDefinition def = new GadgetDefinition();
    def.setSupportsGroups(true);
    gadget.setDefinition(def);
    gadgets.add(gadget);

    String groupAwareness = homeController.getGroupAwareness(gadgets);

    Assert.assertEquals("[true]", groupAwareness);
  }

  @Test
  public void testGroupAwareness_multi() throws Exception {
    List<Gadget> gadgets = new ArrayList<Gadget>();
    Gadget gadget = new Gadget();
    GadgetDefinition def = new GadgetDefinition();
    def.setSupportsGroups(false);
    gadget.setDefinition(def);
    gadgets.add(gadget);

    gadget = new Gadget();
    def = new GadgetDefinition();
    def.setSupportsGroups(true);
    gadget.setDefinition(def);
    gadgets.add(gadget);

    String groupAwareness = homeController.getGroupAwareness(gadgets);

    Assert.assertEquals("[false,true]", groupAwareness);
  }

  @Test
  public void testGetOwnTab() throws Exception {
    MockHttpServletRequest request = getRequest();
    // this is the start tab
    request.setParameter("tab", "1234");

    Person person = (Person) request.getSession().getAttribute(
        LoginInterceptor.PERSON_SESSION_KEY);
    Tab myTab = new Tab();
    myTab.setId(1234L);
    myTab.setOwner(person.getId());
    List<Tab> favoriteTabs = new ArrayList<Tab>();

    TabService tabService = createNiceMock(TabService.class);
    expect(tabService.findByIdAndOwner(1234L, person.getId())).andReturn(myTab);
    replay(tabService);
    autoWireMock(homeController, tabService, TabService.class);

    final Tab requestedTab = homeController.getRequestedTab(request, person, favoriteTabs);
    assertNotNull(requestedTab);
    assertEquals(Long.valueOf(1234L), requestedTab.getId());
  }

  @Test
  public void testGetFavoriteTab() throws Exception {
    MockHttpServletRequest request = getRequest();
    // this is the start tab
    request.setParameter("tab", "1234");

    Person person = (Person) request.getSession().getAttribute(
        LoginInterceptor.PERSON_SESSION_KEY);
    Tab myTab = new Tab();
    myTab.setId(1234L);
    myTab.setOwner(person.getId());
    List<Tab> favoriteTabs = new ArrayList<Tab>();
    favoriteTabs.add(myTab);

    TabService tabService = createNiceMock(TabService.class);
    expect(tabService.findByIdAndOwner(1234L, person.getId())).andReturn(null);
    replay(tabService);
    autoWireMock(homeController, tabService, TabService.class);
    final Tab requestedTab = homeController.getRequestedTab(request, person, favoriteTabs);
    assertNotNull(requestedTab);
    assertEquals(Long.valueOf(1234L), requestedTab.getId());
  }

  @Test
  public void testReturnNullIfNoTab() throws Exception {
    MockHttpServletRequest request = getRequest();
    // this is the start tab
    request.setParameter("tab", "1234");

    Person person = (Person) request.getSession().getAttribute(
        LoginInterceptor.PERSON_SESSION_KEY);
    List<Tab> favoriteTabs = new ArrayList<Tab>();

    TabService tabService = createNiceMock(TabService.class);
    expect(tabService.findByIdAndOwner(1234L, person.getId())).andReturn(null);
    replay(tabService);
    autoWireMock(homeController, tabService, TabService.class);
    final Tab requestedTab = homeController.getRequestedTab(request, person, favoriteTabs);
    assertNull(requestedTab);
  }


}
