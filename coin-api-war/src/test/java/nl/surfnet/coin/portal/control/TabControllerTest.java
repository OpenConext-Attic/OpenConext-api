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

import nl.surfnet.coin.opensocial.service.GroupService;
import nl.surfnet.coin.portal.domain.GadgetDefinition;
import nl.surfnet.coin.portal.domain.Tab;
import nl.surfnet.coin.portal.domain.TemplateTab;
import nl.surfnet.coin.portal.interceptor.LoginInterceptor;
import nl.surfnet.coin.portal.service.TabService;
import nl.surfnet.coin.portal.service.TemplateTabService;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.Returns;
import org.opensocial.models.Person;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link Test} for {@link TabController}
 * 
 */
public class TabControllerTest extends AbstractControllerTest {

  private TabController controller = new TabController();

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.TabController#add(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   */
  @Test
  public void testAdd() throws Exception {
    MockHttpServletRequest request = getRequest();

    TabService tabService = mock(TabService.class);
    Tab tab = new Tab();
    tab.setId(99L);
    Person person = (Person) request.getSession().getAttribute(
        LoginInterceptor.PERSON_SESSION_KEY);
    when(tabService.getCountByOwner(person.getId(), true)).thenReturn(1);
    when(tabService.saveOrUpdate(tab)).thenReturn(tab.getId());
    autoWireMock(controller, tabService, TabService.class);

    request.setParameter("templateTab", "1");

    TemplateTab templateTab = new TemplateTab();
    templateTab.setGadgetDefinitions(Collections
        .singleton(new GadgetDefinition()));
    autoWireMock(controller, new Returns(templateTab), TemplateTabService.class);

    String tabId = controller.add(getModelMap(), request);
    assertNotNull(tabId);
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.TabController#rename(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   * 
   * @throws Exception
   */
  @Test
  public void testRename() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("id", "1");
    request.setParameter("name", "<javascript>+&+</javascript>");

    Tab tab = autoWireTabService();

    boolean rename = controller.rename(getModelMap(), request);
    assertTrue(rename);
    assertEquals("<javascript>+&+</javascript>", tab.getName());
  }

  /*
   * Autowire the controller with a TabService
   */
  private Tab autoWireTabService() throws Exception {
    TabService tabService = mock(TabService.class);
    Tab tab = new Tab();
    tab.setId(1L);
    final Person owner = (Person) getRequest().getSession().getAttribute(
        LoginInterceptor.PERSON_SESSION_KEY);
    String id = owner.getId();
    tab.setOwner(id);
    when(tabService.findByIdAndOwner(1L, owner.getId())).thenReturn(tab);
    when(tabService.saveOrUpdate(tab)).thenReturn(tab.getId());
    autoWireMock(controller, tabService, TabService.class);
    return tab;
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.TabController#remove(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   * 
   * @throws Exception
   */
  @Test
  public void testRemove() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("id", "1");
    request.setParameter("name", "<javascript>+&+</javascript>");

    autoWireTabService();

    boolean remove = controller.remove(getModelMap(), request);
    assertTrue(remove);
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.TabController#reorder(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   * 
   * @throws Exception
   */
  @Test
  public void testReorder() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("ids[]", new String[] { "1", "2", "3" });

    autoWireMock(controller, new Returns(true), TabService.class);

    boolean reorder = controller.reorder(getModelMap(), request);
    assertTrue(reorder);
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.TabController#unsetFavoriteTab(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   * @throws Exception 
   */
  @Test
  public void testUnsetFavoriteTab() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("id", "1");
    request.setParameter("favorite", "true");

    Tab tab = autoWireTabService();

    controller.unsetFavoriteTab(getModelMap(), request);
    assertTrue(tab.getFavorite());
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.TabController#setTabTeam(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   * @throws Exception 
   */
  @Test
  public void testSetTabTeam() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("id", "1");
    request.setParameter("team", "funny-team");

    Tab tab = autoWireTabService();
    autoWireMock(controller, getAllGroups(), GroupService.class);

    controller.setTabTeam(getModelMap(), request);
    assertEquals("funny-team",tab.getTeam());
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.TabController#setTabTeam(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   * @throws Exception
   */
  @Test
  public void testSetMaliciousTabTeam() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("id", "1");
    request.setParameter("team", "alice-team");

    Tab tab = autoWireTabService();
    autoWireMock(controller, getAllGroups(), GroupService.class);

    boolean outcome = controller.setTabTeam(getModelMap(), request);
    assertFalse(outcome);
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.TabController#unsetTabTeam(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   * @throws Exception 
   */
  @Test
  public void testUnsetTabTeam() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("id", "1");
    request.setParameter("team", "funny-team");

    Tab tab = autoWireTabService();

    controller.unsetTabTeam(getModelMap(), request);
    assertNull(tab.getTeam());
  }
}
