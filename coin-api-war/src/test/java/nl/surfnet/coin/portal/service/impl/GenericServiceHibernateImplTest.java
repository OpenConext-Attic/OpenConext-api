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

package nl.surfnet.coin.portal.service.impl;

import nl.surfnet.coin.portal.domain.ClonedTab;
import nl.surfnet.coin.portal.domain.Gadget;
import nl.surfnet.coin.portal.domain.GadgetDefinition;
import nl.surfnet.coin.portal.domain.Invite;
import nl.surfnet.coin.portal.domain.InviteStatus;
import nl.surfnet.coin.portal.domain.SharedGadget;
import nl.surfnet.coin.portal.domain.SharedResource;
import nl.surfnet.coin.portal.domain.SharedResourceType;
import nl.surfnet.coin.portal.domain.SharedTab;
import nl.surfnet.coin.portal.domain.Tab;
import nl.surfnet.coin.portal.domain.TemplateTab;
import nl.surfnet.coin.portal.service.GadgetDefinitionService;
import nl.surfnet.coin.portal.service.GadgetService;
import nl.surfnet.coin.portal.service.InviteService;
import nl.surfnet.coin.portal.service.SharedResourceService;
import nl.surfnet.coin.portal.service.TabService;
import nl.surfnet.coin.portal.service.TemplateTabService;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensocial.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * {@link Test} for {@link nl.surfnet.coin.shared.service.GenericServiceHibernateImpl}
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:coin-portal-context.xml",
    "classpath:coin-portal-properties-hsqldb-context.xml",
    "classpath:coin-shindig-context.xml",
    "classpath:coin-shared-context.xml",
    "classpath:coin-opensocial-context.xml"})
@TransactionConfiguration(transactionManager = "portalTransactionManager", defaultRollback = true)
@Transactional
public class GenericServiceHibernateImplTest {

  private static final String OWNER = "owner";

  @Autowired
  private GadgetDefinitionService gadgetDefinitionService;

  @Autowired
  private GadgetService gadgetService;

  @Autowired
  private SharedResourceService sharedResourceService;

  @Autowired
  private InviteService inviteService;

  @Autowired
  private TabService tabService;

  @Autowired
  private TemplateTabService templateTabService;

  @Autowired
  private SessionFactory portalSessionFactory;
  /*
   * GadgetDefinition object for usage in createGadgets
   */
  private GadgetDefinition gadgetDef;

  private static Long tab1Id = null;

  private Gadget gadget1;
  private Gadget gadget2;

  private static Long date = System.currentTimeMillis();

  private static boolean insertedDbContent = false;

  @BeforeTransaction
  public void setupDbContent() {
    if (!insertedDbContent) {
      createGadgetDefinitions();
      createGadgets();
      createSharedGadgets();
      createTabs();
      createSharedTabs();
      createTemplateTabs();
      insertedDbContent = true;
    }
  }

  /*
   * Set up some TemplateTabs
   */
  private void createTemplateTabs() {
    TemplateTab templateTab1 = new TemplateTab();
    templateTab1.setGadgetDefinitions(new HashSet<GadgetDefinition>(
        gadgetDefinitionService.findAll()));
    templateTab1.setName("Template 1");
    templateTabService.saveOrUpdate(templateTab1);

    // Second tab

    TemplateTab templateTab2 = new TemplateTab();
    templateTab2.setGadgetDefinitions(Collections.singleton(gadgetDef));
    templateTab2.setName("Template 2");

    templateTabService.saveOrUpdate(templateTab2);
  }

  /*
   * Set up some tabs
   */
  private void createTabs() {
    Tab tab1 = new Tab();
    tab1.setFavorite(false);
    addGadgetsToTab(tab1);
    tab1.setName("Test Tab 1");
    tab1.setOrder(0);
    tab1.setTeam("COIN");
    tab1.setOwner(OWNER);

    tabService.saveOrUpdate(tab1);
    tab1Id = tab1.getId();
    // Second tab

    Tab tab2 = new Tab();
    tab2.setFavorite(false);
    tab2.setName("Test Tab 2");
    tab2.setOrder(1);
    tab2.setTeam("COIN DEVELOPERS");
    tab2.setOwner(OWNER);

    tabService.saveOrUpdate(tab2);

    // Third tab

    Tab tab3 = new Tab();
    tab3.setFavorite(true);
    tab3.setName("Test Tab 3");
    tab3.setOrder(2);
    tab3.setOwner("zilverline.com:oharsta");
    tab3.setTeam("Test");
    tab3.setOwner(OWNER);

    tabService.saveOrUpdate(tab3);

  }

  private void addGadgetsToTab(Tab tab) {
    List<Gadget> gadgets = gadgetService.findAll();
    for (Gadget gadget : gadgets) {
      tab.addGadget(gadget);
    }
  }

  /*
   * Set up some Gadget definitions
   */
  private void createGadgetDefinitions() {
    gadgetDef = new GadgetDefinition();
    gadgetDef.setAdded(new Date(date));
    gadgetDef.setApproved(true);
    gadgetDef.setAuthor("everett.nl:swelberg");
    gadgetDef.setAuthorEmail("stein.welberg@everett.nl");
    gadgetDef.setDescription("Veeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeery"
        + " long description!");
    gadgetDef.setInstallCount(100000000);
    gadgetDef.setScreenshot("http://www.example.com/screenshot.png");
    gadgetDef.setSupportsGroups(true);
    gadgetDef.setSupportsSSO(false);
    gadgetDef.setThumbnail("http://www.example.com/thumb.png");
    gadgetDef.setTitle("Loooooooooooooooooooooooooooooooooo"
        + "oooooooooooooooooooooooong title");
    gadgetDef.setUrl("http://www.example.com/gadget.xml");

    gadgetDefinitionService.saveOrUpdate(gadgetDef);
  }

  /*
   * Set up some Gadgets
   */
  private void createGadgets() {
    gadget1 = new Gadget();

    gadget1.setOrder(0);
    gadget1.setColumn(1);
    gadget1.setHasPermission(false);
    gadget1.setPrefs("Preference1");
    gadget1.setDefinition(gadgetDef);
    gadget1.setTeam("urn:collab:group:diensten:test.surfgroups.nl:test_team");

    gadgetService.saveOrUpdate(gadget1);

    // Second Gadget

    gadget2 = new Gadget();

    gadget2.setOrder(1);
    gadget2.setColumn(1);
    gadget2.setHasPermission(false);
    gadget2.setPrefs("Preference 2");
    gadget2.setDefinition(gadgetDef);

    gadgetService.saveOrUpdate(gadget2);

  }

  private void createSharedGadgets() {
    SharedGadget sharedGadget = new SharedGadget();
    sharedGadget.setPrototype(gadget2);
    sharedGadget.setSharedBy("zilerline.com:oharsta");
    sharedGadget.setTimestamp(date);
    sharedGadget.setTeamTitle("COIN Test Team");
    sharedGadget.setSharedByDisplayName("Okke Harsta");

    sharedResourceService.saveOrUpdate(sharedGadget);
  }

  private void createSharedTabs() {
    SharedTab sharedTab = new SharedTab();
    sharedTab.setPrototype(tabService.findById(tab1Id));
    sharedTab.setSharedBy("everett.nl:swelberg");
    sharedTab.setSharedByDisplayName("Stein Welberg");
    sharedTab.setTeamTitle("Team COIN");
    sharedTab.setTimestamp(date);

    Invite invite = new Invite();
    invite.setInvitee(OWNER);
    invite.setStatus(InviteStatus.OPEN);
    sharedTab.addInvite(invite);

    invite = new Invite();
    invite.setInvitee("ignore");
    sharedTab.addInvite(invite);

    sharedResourceService.saveOrUpdate(sharedTab);

    SharedTab sharedTab2 = new SharedTab();
    sharedTab2.setPrototype(tabService.findById(tab1Id));
    sharedTab2.setSharedBy("everett.nl:swelberg");
    sharedTab2.setTimestamp(date);

    sharedResourceService.saveOrUpdate(sharedTab2);
  }

  @After
  public void flushSession() {
    // flushing forces the actual sql to be executed
    portalSessionFactory.getCurrentSession().flush();
  }

  @Test
  public void testFindTabs() {
    List<Tab> tabs = tabService.findAll();
    assertEquals(3, tabs.size());
    int count = tabService.getCount();
    assertEquals(3, count);
    for (Tab tab : tabs) {
      assertTrue(tab.getCreationTimestamp() > 0);
    }
  }

  @Test
  public void testFindGagdetDefinitions() {
    List<GadgetDefinition> gadgetDefs = gadgetDefinitionService.findAll();
    assertEquals(1, gadgetDefs.size());
    GadgetDefinition gadgetDefinition = gadgetDefs.get(0);

    assertEquals(new Date(date), gadgetDefinition.getAdded());
    assertEquals("everett.nl:swelberg", gadgetDefinition.getAuthor());
    assertEquals("stein.welberg@everett.nl", gadgetDefinition.getAuthorEmail());
    assertEquals("Veeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
        + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeery"
        + " long description!", gadgetDefinition.getDescription());
    assertEquals(100000000, gadgetDefinition.getInstallCount());
    assertEquals("http://www.example.com/screenshot.png",
        gadgetDefinition.getScreenshot());
    assertEquals(true, gadgetDefinition.isSupportsGroups());
    assertEquals(false, gadgetDefinition.isSupportsSSO());
    assertEquals(true, gadgetDefinition.isApproved());
    assertEquals("http://www.example.com/thumb.png",
        gadgetDefinition.getThumbnail());
    assertEquals("Loooooooooooooooooooooooooooooooooo"
        + "oooooooooooooooooooooooong title", gadgetDefinition.getTitle());
    assertEquals("http://www.example.com/gadget.xml", gadgetDefinition.getUrl());
  }

  @Test
  public void testFindGadgets() {
    List<Gadget> gadgets = gadgetService.findAll();
    assertEquals(2, gadgets.size());
    for (Gadget gadget : gadgets) {
      assertTrue(gadget.getCreationTimestamp() > 0);
    }
    Gadget gadget = gadgets.get(0);

    assertEquals(0, gadget.getOrder());
    assertEquals(1, gadget.getColumn());
    assertEquals(false, gadget.isHasPermission());
    assertEquals("Preference1&groupContext=COIN", gadget.getPrefs());
  }

  @Test
  public void testFindSharedResource() {
    List<SharedResource> all = sharedResourceService.findAll();
    assertEquals(3, all.size());

    // get the shared Gadget
    SharedGadget sharedGadget = (SharedGadget) all.get(0);
    assertEquals(date, sharedGadget.getTimestamp());
    assertEquals("zilerline.com:oharsta", sharedGadget.getSharedBy());
    assertEquals("Loooooooooooooooooooooooooooooooooo"
        + "oooooooooooooooooooooooong title", sharedGadget.getName());
    assertEquals(SharedResourceType.Gadget, sharedGadget.getType());
    assertEquals("http://www.example.com/gadget.xml", sharedGadget
        .getPrototype().getDefinition().getUrl());

    // get the shared Tab
    SharedTab sharedTab = (SharedTab) all.get(1);

    assertEquals(SharedResourceType.Tab, sharedTab.getType());
    assertEquals("everett.nl:swelberg", sharedTab.getSharedBy());
    assertEquals("Test Tab 1", sharedTab.getPrototype().getName());
    assertEquals("COIN", sharedTab.getTeam());

    // TODO does not function yet..
    // assertEquals(date.toString(), sharedTab.getTimestamp());

  }

  @Test
  public void testFindFavoritesTab() {
    Person owner = new Person();
    owner.setField("id", OWNER);
    List<Tab> favorites = tabService.findFavorites(owner.getId(), false);
    assertEquals(2, favorites.size());

    favorites = tabService.findFavorites(owner.getId(), true);
    assertEquals(1, favorites.size());
  }

  @Test
  public void testTemplateTab() {
    TemplateTab templateTab = new TemplateTab();
    templateTab.setName("test-tt");
    templateTabService.saveOrUpdate(templateTab);
    assertNotNull(templateTab.getId());
    List<TemplateTab> templates = templateTabService.findAll();
    assertEquals(3, templates.size());
  }

  @Test
  public void testTemplateTabTransactionalRollback() {
    List<TemplateTab> templates = templateTabService.findAll();
    assertEquals(2, templates.size());
  }

  @Test
  public void findRecentInvitesByInvitee() {
    Person person = new Person();
    person.setField("id", OWNER);
    List<Invite> findByInvitee = inviteService.findRecentByInvitee(person.getId(),
        InviteStatus.OPEN);
    assertEquals(1, findByInvitee.size());
    assertTrue(findByInvitee.get(0).getCreationTimestamp() > 0);
  }
  
  @Test
  public void findExpiredInvitesByInvitee() {
    SharedTab sharedTab = new SharedTab();

    Invite invite = new Invite();
    invite.setInvitee(OWNER);
    invite.setStatus(InviteStatus.OPEN);
    invite.setCreationTimestamp(date - InviteServiceHibernateImpl.RECENT_MS);
    sharedTab.addInvite(invite);
    
    sharedResourceService.saveOrUpdate(sharedTab);
    
    Person person = new Person();
    person.setField("id", OWNER);
    
    List<Invite> findByInvitee = inviteService.findRecentByInvitee(person.getId(),
        InviteStatus.OPEN);
    assertEquals(1, findByInvitee.size());
  }

  @Test
  public void testFindByPersonAndFavoriteCount() {
    int countByOwner = tabService.getCountByOwner(OWNER, true);
    int countByOwner2 = tabService.getCountByOwner(OWNER, false);
    assertEquals(1, countByOwner);
    assertEquals(2, countByOwner2);
  }

  @Test
  public void testReorderGadgetsAllToColumn2() {
    Tab tab = tabService.findById(tab1Id);
    String[] column1 = null;
    String[] column2 = new String[2];

    column2[0] = "1";
    column2[1] = "2";

    gadgetService.reorderGadgets(column1, column2, tab);

    Tab reorderedTab = tabService.findById(1L);
    List<Gadget> gadgets = reorderedTab.getGadgets();
    int i = 0;
    for (Gadget gadget : gadgets) {
      assertEquals(2, gadget.getColumn());
      assertEquals(i, gadget.getOrder());
      i++;
    }
  }

  @Test
  public void testReorderGadgetsAllToColumn1() {
    Tab tab = tabService.findById(1L);
    String[] column1 = new String[2];
    String[] column2 = null;

    column1[0] = "1";
    column1[1] = "2";

    gadgetService.reorderGadgets(column1, column2, tab);

    Tab reorderedTab = tabService.findById(1L);
    List<Gadget> gadgets = reorderedTab.getGadgets();
    int i = 0;
    for (Gadget gadget : gadgets) {
      assertEquals(1, gadget.getColumn());
      assertEquals(i, gadget.getOrder());
      i++;
    }
  }

  @Test
  public void testClearFirstSpot() {
    Tab tab = tabService.findById(1L);
    String[] column1 = new String[2];

    column1[0] = "1";
    column1[1] = "2";

    gadgetService.clearFirstSpot(tab);

    List<Gadget> gadgets = tab.getGadgets();

    int i = 1;
    for (Gadget gadget : gadgets) {
      assertEquals(1, gadget.getColumn());
      assertEquals(i, gadget.getOrder());
      i++;
    }
  }

  @Test
  public void testDeleteGadget() {
    int allGadgetsBefore = gadgetService.getCount();
    Gadget gadgetToDelete = gadgetService.findById(1L);
    gadgetToDelete.getTab().getGadgets().remove(gadgetToDelete);
    gadgetService.delete(gadgetToDelete);
    flushSession();
    int allGadgetsAfter = gadgetService.getCount();

    assertEquals(allGadgetsBefore, allGadgetsAfter + 1);
  }

  @Test
  public void testGetCountByTab() {
    Tab tab = tabService.findById(tab1Id);
    addGadgetsToTab(tab);
    assertEquals(2, gadgetService.getCountByTab(tab));
    assertEquals(0, gadgetService.getCountByTab(tabService.findById(2L)));
  }

  @Test
  public void testGetCountByTabColumn() {
    Tab tab = tabService.findById(tab1Id);
    addGadgetsToTab(tab);
    assertEquals(2, gadgetService.getCountByTabColumn(tab, 1));
    assertEquals(0, gadgetService.getCountByTabColumn(tab, 2));
  }

  @Test
  public void testGetByTabColumn() {
    Tab tab = tabService.findById(tab1Id);
    addGadgetsToTab(tab);
    assertEquals(2, gadgetService.getByTabColumn(tab, 1).size());
    assertEquals(0, gadgetService.getByTabColumn(tab, 2).size());
  }

  @Test
  public void testCloneTab() {
    Tab tab = tabService.findById(tab1Id);
    addGadgetsToTab(tab);
    ClonedTab clonedTab = tabService.cloneTab(tab, tab.getGadgets());
    assertEquals(tab.getName(), clonedTab.getName());
    assertEquals(tab.getTeam(), clonedTab.getTeam());
    assertEquals(tab.getGadgets().size(), clonedTab.getGadgets().size());
    Object[] gadgets = tab.getGadgets().toArray();
    Object[] clonedGadgets = clonedTab.getGadgets().toArray();

    for (int i = 0; i < clonedGadgets.length; i++) {
      assertEquals(((Gadget) gadgets[i]).getColumn(),
          ((Gadget) clonedGadgets[i]).getColumn());
      assertEquals(((Gadget) gadgets[i]).getDefinition(),
          ((Gadget) clonedGadgets[i]).getDefinition());
      assertEquals(((Gadget) gadgets[i]).getOrder(),
          ((Gadget) clonedGadgets[i]).getOrder());
      assertEquals(((Gadget) gadgets[i]).getPrefs(),
          ((Gadget) clonedGadgets[i]).getPrefs());
    }
  }

}
