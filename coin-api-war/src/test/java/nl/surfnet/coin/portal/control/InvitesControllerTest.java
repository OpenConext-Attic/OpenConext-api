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
import nl.surfnet.coin.portal.domain.Gadget;
import nl.surfnet.coin.portal.domain.Invite;
import nl.surfnet.coin.portal.domain.InviteStatus;
import nl.surfnet.coin.portal.domain.SharedTab;
import nl.surfnet.coin.portal.domain.Tab;
import nl.surfnet.coin.portal.service.GadgetService;
import nl.surfnet.coin.portal.service.InviteService;
import nl.surfnet.coin.portal.service.TabService;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Group;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.Returns;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link Test} for {@link InvitesController}
 * 
 */
public class InvitesControllerTest extends AbstractControllerTest {

  private InvitesController controller = new InvitesController();

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.portal.control.AbstractControllerTest#setup()
   */
  @Before
  @Override
  public void setup() throws Exception {
    super.setup();
    autoWireMock(controller, getGroupReturn(), GroupService.class);
    Invite invite = createInvite();
    autoWireMock(controller, new Returns(invite), InviteService.class);
    autoWireRemainingResources(controller);
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.InvitesController#invites(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   */
  @Test
  public void testInvites() throws Exception {
    Invite invite = new Invite();
    invite.setInvitee(getPerson().getId());
    InviteService inviteService = mock(InviteService.class);
    when(inviteService.findRecentByInvitee(getPerson().getId(), InviteStatus.OPEN))
        .thenReturn(Collections.singletonList(invite));
    autoWireMock(controller, inviteService, InviteService.class);

    controller.invites(getModelMap(), getRequest());
    Map<Group, List<Activity>> groupedActivities = (Map<Group, List<Activity>>) getModelMap()
        .get("groupedActivities");
    assertNotNull(groupedActivities);
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.InvitesController#showInviteDetail(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   */
  @Test
  public void testShowInviteDetail() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.addParameter("id", "1");

    ModelMap modelMap = getModelMap();
    String view = controller.showInviteDetail(modelMap, request);
    assertEquals("invite-detail-tab", view);
  }

  /*
   * Create Invite
   */
  private Invite createInvite() {
    Invite invite = new Invite();
    invite.setInvitee(getPerson().getId());
    SharedTab sharedResource = new SharedTab();
    Tab tab = new Tab();
    tab.addGadget(new Gadget());
    tab.setOwner(getPerson().getId());
    sharedResource.setPrototype(tab);
    invite.setSharedResource(sharedResource);
    return invite;
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.InvitesController#moreActivities(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   */
  @Test
  public void testMoreActivities() {
    String view = controller.moreActivities(getModelMap(), getRequest());
    assertEquals(view, "activities");
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.InvitesController#deny(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   * 
   * @throws Exception
   */
  @Test
  public void testDeny() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.addParameter("id", "1");

    Invite invite = new Invite();
    invite.setInvitee(getPerson().getId());
    InviteService inviteService = mock(InviteService.class);

    when(inviteService.findById(1L)).thenReturn(invite);
    when(inviteService.saveOrUpdate(invite)).thenReturn(invite.getId());
    autoWireMock(controller, inviteService, InviteService.class);

    boolean success = controller.deny(getModelMap(), request);
    assertTrue(success);
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.control.InvitesController#accept(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}
   * .
   */
  @Test
  public void testAccept() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.addParameter("inviteId", "1");
    request.addParameter("add-gadget", new String[] { "1", "2", "3" });
    request.addParameter("share-info", new String[] { "1", "2" });

    Invite invite = createInvite();
    InviteService inviteService = mock(InviteService.class);
    when(inviteService.findById(1L)).thenReturn(invite);
    when(inviteService.saveOrUpdate(invite)).thenReturn(1L);
    autoWireMock(controller, inviteService, InviteService.class);
    Gadget gadget = new Gadget();
    Tab tab = new Tab();
    tab.setOwner(getPerson().getId());
    gadget.setTab(tab);
    autoWireMock(controller, new Returns(gadget), GadgetService.class);
    autoWireMock(controller, new Returns(1L), TabService.class);
    RedirectView redirect = (RedirectView) controller.accept(getModelMap(),
        request);
    assertNotNull(redirect);
  }

}
