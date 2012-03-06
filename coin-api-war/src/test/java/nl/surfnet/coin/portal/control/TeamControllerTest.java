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
import nl.surfnet.coin.opensocial.service.PersonService;
import nl.surfnet.coin.portal.domain.ClonedTab;
import nl.surfnet.coin.portal.domain.Gadget;
import nl.surfnet.coin.portal.domain.SharedTab;
import nl.surfnet.coin.portal.domain.Tab;
import nl.surfnet.coin.portal.interceptor.LoginInterceptor;
import nl.surfnet.coin.portal.service.GadgetService;
import nl.surfnet.coin.portal.service.SharedResourceService;
import nl.surfnet.coin.portal.service.TabService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.Returns;
import org.opensocial.models.Person;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link Test} for {@link TeamController}
 * 
 */
public class TeamControllerTest extends AbstractControllerTest {
  
  private TeamController controller = new TeamController();

  /**
   * Test the happy flow without exceptions
   * 
   * @throws Exception
   *           in case of an unexpected Exception
   */
  @Test
  public void testTeamControllerHappyFlow() throws Exception {
    Tab tab = new Tab();
    tab.setTeam("dummy-team");
    tab.setOwner("1");
    autoWireMock(controller, new Returns(tab), TabService.class);
    
    getRequest().addParameter("tab", "1");

    String viewName = controller.gadgetOverview(getModelMap(), getRequest());
    assertEquals("slidedown/team-settings", viewName);
    Tab modelTab = (Tab) getModelMap().get("requestedTab");
    assertEquals(modelTab.getTeam(),"dummy-team");
  }

  
  /**
   * Test the happy flow without exceptions
   * 
   * @throws Exception
   *           in case of an unexpected Exception
   */
  @Test
  public void testSendSnapshotHappyFlow() throws Exception {
    Gadget gadget = new Gadget();
    gadget.setId(1L);
    autoWireMock(controller,new Returns(gadget),GadgetService.class );

    TabService tabService = mock(TabService.class);
    Tab tab = new Tab();
    tab.setTeam("dummy-team");
    tab.setName("name");
    tab.setOwner("test");
    final Person owner = (Person) getRequest().getSession().getAttribute(LoginInterceptor.PERSON_SESSION_KEY);
    String id = owner.getId();
    tab.setOwner(id);
    when(tabService.findByIdAndOwner(1L, owner.getId())).thenReturn(tab);
    when(tabService.cloneTab(tab, Collections.singletonList(gadget))).thenReturn(new ClonedTab());
    autoWireMock(controller, tabService, TabService.class);
    
    autoWireMock(controller, new FixedLocaleResolver(Locale.ENGLISH), LocaleResolver.class);
    
    SharedTab sharedTab = new SharedTab();
    sharedTab.setPrototype(tab);
    SharedResourceService sharedResourceService = mock(SharedResourceService.class);
    when(sharedResourceService.findById(1L)).thenReturn(sharedTab);
    when(sharedResourceService.saveOrUpdate(sharedTab)).thenReturn(1L);
    autoWireMock(controller,sharedResourceService,SharedResourceService.class );
    
    autoWireRemainingResources(controller);
    
    MockHttpServletRequest request = getRequest();
    //tabId
    request.addParameter("id", "1");
    //groupId
    request.addParameter("team", "dummy-team");
    request.addParameter("message", "dummy-message");
    request.addParameter("invitees[]", new String[]{"1"});
    request.addParameter("gadgets[]", new String[]{"1"});
  
    String viewName = controller.sendSnapshot(getModelMap(), request);
    assertEquals("succes", viewName);
    assertTrue(getModelMap().isEmpty());
  }

  /**
   * Test the happy flow without exceptions
   *
   * @throws Exception
   *           in case of an unexpected Exception
   */
  @Test
  public void testSendMaliciousSnapshot() throws Exception {
    Gadget gadget = new Gadget();
    gadget.setId(1L);
    autoWireMock(controller,new Returns(gadget),GadgetService.class );

    TabService tabService = mock(TabService.class);
    Tab tab = new Tab();
    tab.setTeam("dummy-team");
    tab.setName("name");
    tab.setOwner("test");
    tab.setOwner("eve");
    when(tabService.findById(1L)).thenReturn(tab);
    when(tabService.cloneTab(tab, Collections.singletonList(gadget))).thenReturn(new ClonedTab());
    autoWireMock(controller, tabService, TabService.class);

    autoWireMock(controller, new FixedLocaleResolver(Locale.ENGLISH), LocaleResolver.class);

    SharedTab sharedTab = new SharedTab();
    sharedTab.setPrototype(tab);
    SharedResourceService sharedResourceService = mock(SharedResourceService.class);
    when(sharedResourceService.findById(1L)).thenReturn(sharedTab);
    when(sharedResourceService.saveOrUpdate(sharedTab)).thenReturn(1L);
    autoWireMock(controller,sharedResourceService,SharedResourceService.class );

    autoWireRemainingResources(controller);

    MockHttpServletRequest request = getRequest();
    request.addParameter("id", "1"); // tab id
    request.addParameter("team", "dummy-team"); // group id
    request.addParameter("message", "dummy-message");
    request.addParameter("invitees[]", new String[]{"1"});
    request.addParameter("gadgets[]", new String[]{"1"});

    String viewName = controller.sendSnapshot(getModelMap(), request);
    assertEquals("error", viewName);
    assertTrue(getModelMap().isEmpty());
  }
  
  @Before
  @Override
  public void setup() throws Exception {
    super.setup();
    Person person = new Person();
    person.setField("id", "test");
    person.setField("displayName", "test");
    List<Person> persons = Collections.singletonList(person);
    autoWireMock(controller, new Returns(persons), PersonService.class);
    autoWireMock(controller, getGroupReturn(), GroupService.class);
  }
}
