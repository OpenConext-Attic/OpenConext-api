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
import nl.surfnet.coin.portal.domain.Tab;
import nl.surfnet.coin.portal.interceptor.LoginInterceptor;
import nl.surfnet.coin.portal.service.TabService;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.Returns;
import org.opensocial.models.Person;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertEquals;

/**
 * {@link Test} for {@link TeamsController}
 * 
 */
public class TeamsControllerTest extends AbstractControllerTest {

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.TeamsController#teams(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   * 
   * @throws Exception if something goes wrong
   */
  @SuppressWarnings({"unchecked"})
  @Test
  public void testTeams() throws Exception {
    TeamsController controller = new TeamsController();
    HttpServletRequest request = getRequest();

    autoWireMock(controller, getGroupReturn(), GroupService.class);
    autoWireRemainingResources(controller);

    ModelMap modelMap = getModelMap();
    String view = controller.teams(modelMap, request);
    assertEquals("teams", view);
  }

  @SuppressWarnings({"unchecked"})
  @Test
  public void testGetGroupMembers() throws Exception {
    TeamsController controller = new TeamsController();
    HttpServletRequest request = getRequest();
    Person person = (Person) request.getSession().getAttribute(LoginInterceptor.PERSON_SESSION_KEY);

    
    Tab tab = new Tab();
    tab.setTeam("dummy-team");
    tab.setOwner(person.getId());

    autoWireMock(controller, new Returns(Collections.singletonList(tab)),
        TabService.class);

    List<Person> persons = Collections.singletonList(person);
    autoWireMock(controller, new Returns(persons), PersonService.class);
    autoWireMock(controller, getGroupReturn(), GroupService.class);

    ModelMap modelMap = getModelMap();
    String view = controller.getTeamMembers(modelMap, request, "dummy-team");
    assertEquals("myteamdetails", view);
    List<Person> members = (List<Person>) modelMap.get("groupMembers");
    assertEquals(person, members.get(0));
    List<Tab> groups = (List<Tab>) modelMap.get("groupTabs");
    assertEquals(tab, groups.get(0));
  }

}
