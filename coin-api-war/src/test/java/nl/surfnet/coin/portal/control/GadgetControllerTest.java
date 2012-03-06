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

import nl.surfnet.coin.portal.domain.Gadget;
import nl.surfnet.coin.portal.domain.GadgetDefinition;
import nl.surfnet.coin.portal.domain.Tab;
import nl.surfnet.coin.portal.interceptor.LoginInterceptor;
import nl.surfnet.coin.portal.service.GadgetDefinitionService;
import nl.surfnet.coin.portal.service.GadgetService;
import nl.surfnet.coin.portal.service.MetadataProvider;
import nl.surfnet.coin.portal.service.TabService;
import nl.surfnet.coin.portal.service.UserPreferencesService;
import nl.surfnet.coin.portal.util.CoinEnvironment;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.Returns;
import org.opensocial.models.Person;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author oharsta
 */
public class GadgetControllerTest extends AbstractControllerTest {

  private GadgetController controller = new GadgetController();

  /**
   * Test method for
   * {@link GadgetController#gadgetOverview(org.springframework.ui.ModelMap)}
   * .
   */
  @Test
  public void testGadgetOverview() {
    String view = controller.gadgetOverview(getModelMap());
    assertEquals("View", "gadgetoverview", view);
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.GadgetController#searchGadget(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   */
  @Test
  public void testSearchGadget() {
    getRequest().addParameter("gadgetQuery", "%test%");
    String view = controller.searchGadget(getModelMap(), getRequest());
    assertEquals("View", "gadgetoverview", view);
  }

  /**
   * Test method for
   * {@link GadgetController#addGadget(javax.servlet.http.HttpServletRequest)}
   * .
   * @throws Exception if something goes wrong
   */
  @Test
  public void testAddGadget() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("tab", "1");
    request.setParameter("gadget", "1");

    UserPreferencesService userPreferencesService = mock(UserPreferencesService.class);
    autoWireMock(controller, userPreferencesService, UserPreferencesService.class);

    TabService tabService = mock(TabService.class);
    Tab tab = new Tab();
    tab.setId(99L);
    tab.setOwner(getPerson().getId());
    when(tabService.findByIdAndOwner(1L, getPerson().getId())).thenReturn(tab);
    when(tabService.saveOrUpdate(tab)).thenReturn(tab.getId());
    autoWireMock(controller, tabService, TabService.class);

    GadgetDefinitionService gadgetDefinitionService = mock(GadgetDefinitionService.class);
    GadgetDefinition gadgetDefinition = new GadgetDefinition();
    when(gadgetDefinitionService.findById(1L)).thenReturn(gadgetDefinition);
    when(gadgetDefinitionService.saveOrUpdate(gadgetDefinition)).thenReturn(1L);
    autoWireMock(controller, gadgetDefinitionService,
        GadgetDefinitionService.class);

    String tabId = controller.addGadget(request);
    assertEquals("TabId", "99", tabId);
  }


  /**
   * Test method for
   * {@link GadgetController#addGadget(javax.servlet.http.HttpServletRequest)}
   *
   * Check whether you can add a gadget to someone else's tab
   */
  @Test
  public void testAddMaliciousGadget() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("tab", "1");
    request.setParameter("gadget", "1");

    TabService tabService = mock(TabService.class);
    Tab tab = new Tab();
    tab.setId(99L);
    tab.setOwner("eve");
    when(tabService.findById(1L)).thenReturn(tab);
    when(tabService.saveOrUpdate(tab)).thenReturn(tab.getId());
    autoWireMock(controller, tabService, TabService.class);

    GadgetDefinitionService gadgetDefinitionService = mock(GadgetDefinitionService.class);
    GadgetDefinition gadgetDefinition = new GadgetDefinition();
    when(gadgetDefinitionService.findById(1L)).thenReturn(gadgetDefinition);
    when(gadgetDefinitionService.saveOrUpdate(gadgetDefinition)).thenReturn(1L);
    autoWireMock(controller, gadgetDefinitionService,
        GadgetDefinitionService.class);

    String tabId = controller.addGadget(request);
    assertEquals("TabId", "error", tabId);
  }

  /**
   * Test method for
   * {@link GadgetController#reorder(javax.servlet.http.HttpServletRequest)}
   * .
   *
   * @throws Exception
   */
  @Test
  public void testReorder() throws Exception {
    Tab tab = new Tab();
    tab.setId(1L);
    tab.setOwner(getPerson().getId());

    TabService tabService = mock(TabService.class);
    when(tabService.findByIdAndOwner(1L, getPerson().getId())).thenReturn(tab);
    autoWireMock(controller, tabService, TabService.class);
    
    getRequest().setParameter("tabId", "1");
    autoWireMock(controller, new Returns(true), GadgetService.class);
    boolean redirect = controller.reorder(getRequest());
    assertTrue(redirect);
  }

  /**
   * Test method for
   * {@link GadgetController#delete(javax.servlet.http.HttpServletRequest)}
   * .
   */
  @Test
  public void testDelete() {
    getRequest().setParameter("gadget", "1");
    boolean success = controller.delete(getRequest());
    assertTrue("Successfully deleted gadget", success);
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.GadgetController#addCustomGadget(javax.servlet.http.HttpServletRequest)}
   */
  @Test
  public void testAddCustomGadget() throws Exception {
    String gadgetUrl = "http://igooglegadget.buienradar.nl/buienradar.xml";

    MockHttpServletRequest request = getRequest();
    request.setParameter("tab", "1");
    request.setParameter("gadgetUrl", gadgetUrl);

    CoinEnvironment env = new CoinEnvironment();
    env.setAllowCustomGadgets(true);
    controller.setEnvironment(env);

    TabService tabService = mock(TabService.class);
    Tab tab = new Tab();
    tab.setId(99L);
    tab.setOwner(getPerson().getId());
    when(tabService.findByIdAndOwner(1L, getPerson().getId())).thenReturn(tab);
    when(tabService.saveOrUpdate(tab)).thenReturn(tab.getId());
    autoWireMock(controller, tabService, TabService.class);

    GadgetDefinitionService gadgetDefinitionService = mock(GadgetDefinitionService.class);
    when(gadgetDefinitionService.searchByUrl(gadgetUrl)).thenReturn(null);
    GadgetDefinition gadgetDefinition = new GadgetDefinition();
    gadgetDefinition.setUrl(gadgetUrl);
    when(gadgetDefinitionService.saveOrUpdate(gadgetDefinition)).thenReturn(1L);
    autoWireMock(controller, gadgetDefinitionService, GadgetDefinitionService.class);

    MetadataProvider testProvider = customGadgetProvider();
    controller.setMetadataProvider(testProvider);

    String tabId = controller.addCustomGadget(request);
    assertEquals("TabId after adding custom gadget", "99", tabId);
  }

  private static MetadataProvider customGadgetProvider() {
    return new MetadataProvider() {
      @Override
      public String getMetaData(List<Gadget> gadgets) {
        try {
          ClassPathResource resource = new ClassPathResource("jsons/mock-gadget-metadata.json");
          return IOUtils.toString(resource.getInputStream());
        } catch (IOException e) {
          System.err.print("Could not read test resource" + e);
          return "";
        }
      }
    };
  }

  /**
   * Tests if "error" is returned when
   *
   * @throws Exception
   */
  @Test
  public void testForceAddCustomGadgetWhenNotAllowed() throws Exception {
    String gadgetUrl = "http://igooglegadget.buienradar.nl/buienradar.xml";

    MockHttpServletRequest request = getRequest();
    request.setParameter("tab", "1");
    request.setParameter("gadgetUrl", gadgetUrl);

    CoinEnvironment env = new CoinEnvironment();
    env.setAllowCustomGadgets(false);
    controller.setEnvironment(env);

    String tabId = controller.addCustomGadget(request);
    assertEquals("TabId after adding custom gadget when not allowed", "error", tabId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.portal.control.AbstractControllerTest#setup()
   */
  @Before
  @Override
  public void setup() throws Exception {
    super.setup();

    TabService tabService = mock(TabService.class);
    Tab tab = new Tab();
    tab.setId(1L);
    String id = ((Person) getRequest().getSession().getAttribute(
        LoginInterceptor.PERSON_SESSION_KEY)).getId();
    tab.setOwner(id);
    when(tabService.findById(1L)).thenReturn(tab);
    when(tabService.saveOrUpdate(tab)).thenReturn(tab.getId());
    autoWireMock(controller, tabService, TabService.class);
    Gadget gadget = new Gadget();
    gadget.setTab(tab);
    autoWireMock(controller, new Returns(gadget), GadgetService.class);
    super.autoWireRemainingResources(controller);
  }

}
