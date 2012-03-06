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

import nl.surfnet.coin.portal.domain.GadgetDefinition;
import nl.surfnet.coin.portal.domain.GadgetDefinitionStatus;
import nl.surfnet.coin.portal.service.GadgetDefinitionService;
import nl.surfnet.coin.shared.service.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:coin-portal-context.xml",
    "classpath:coin-portal-properties-hsqldb-context.xml",
    "classpath:coin-shindig-context.xml",
    "classpath:coin-shared-context.xml",
    "classpath:coin-opensocial-context.xml"})
@TransactionConfiguration(transactionManager = "portalTransactionManager", defaultRollback = true)
@Transactional
public class GadgetDefinitionServiceHibernateImplTest {
  @Autowired
  private GadgetDefinitionService gadgetDefinitionService;

  @Test
  public void findAllPublishedShouldBe0() {
    List<GadgetDefinition> defs = gadgetDefinitionService.findAllPublished();
    assertEquals(defs.size(), 0);
  }

  @Test
  public void findAllPublished1DefaultShouldBePublished() {
    GadgetDefinition def = new GadgetDefinition();
    gadgetDefinitionService.saveOrUpdate(def);
    List<GadgetDefinition> defs = gadgetDefinitionService.findAllPublished();
    assertEquals(defs.size(), 1);
    assertEquals(defs.get(0), def);
  }

  @Test
  public void findAllPublished1Published() {
    GadgetDefinition def = addDef(GadgetDefinitionStatus.PUBLISHED);
    List<GadgetDefinition> defs = gadgetDefinitionService.findAllPublished();
    assertEquals(defs.size(), 1);
    assertEquals(defs.get(0), def);
  }

  @Test
  public void findAllPublished1Unpublished() {
    addDef(GadgetDefinitionStatus.UNPUBLISHED);
    List<GadgetDefinition> defs = gadgetDefinitionService.findAllPublished();
    assertEquals(defs.size(), 0);
  }

  @Test
  public void findAllPublishedVarious() {
    addDef(GadgetDefinitionStatus.PUBLISHED);
    addDef(GadgetDefinitionStatus.UNPUBLISHED);
    addDef(GadgetDefinitionStatus.TEST);
    addDef(GadgetDefinitionStatus.PUBLISHED);
    List<GadgetDefinition> defs = gadgetDefinitionService.findAllPublished();
    assertEquals(defs.size(), 2);
  }

  @Test
  public void findAllPublishedExcludeCustom() {
    addDef(GadgetDefinitionStatus.PUBLISHED);
    GadgetDefinition def = new GadgetDefinition();
    def.setStatus(GadgetDefinitionStatus.PUBLISHED);
    def.setCustomGadget(true);
    gadgetDefinitionService.saveOrUpdate(def);
    List<GadgetDefinition> allPubDefs = gadgetDefinitionService.findAllPublished();
    assertEquals(allPubDefs.size(), 2);
    List<GadgetDefinition> allPubExCustom = gadgetDefinitionService.findAllPublishedExcludeCustom();
    assertEquals(allPubExCustom.size(), 1);
  }

  @Test
  public void findAllPublishedExcludeCustomOrderedByPopularity() {
    GadgetDefinition def = new GadgetDefinition();
    def.setStatus(GadgetDefinitionStatus.PUBLISHED);
    def.setTitle("Three installs");
    def.setInstallCount(3);
    gadgetDefinitionService.saveOrUpdate(def);

    def = new GadgetDefinition();
    def.setStatus(GadgetDefinitionStatus.PUBLISHED);
    def.setTitle("Twelve installs");
    def.setInstallCount(12);
    gadgetDefinitionService.saveOrUpdate(def);

    def = new GadgetDefinition();
    def.setStatus(GadgetDefinitionStatus.PUBLISHED);
    def.setTitle("One install");
    def.setInstallCount(1);
    gadgetDefinitionService.saveOrUpdate(def);

    def = new GadgetDefinition();
    def.setStatus(GadgetDefinitionStatus.PUBLISHED);
    def.setTitle("Thirty installs custom");
    def.setInstallCount(30);
    def.setCustomGadget(true);
    gadgetDefinitionService.saveOrUpdate(def);

    List<GadgetDefinition> allPubDefs = gadgetDefinitionService.findAllPublished();
    assertEquals("4 published", allPubDefs.size(), 4);
    List<GadgetDefinition> allPubExCustom =
        gadgetDefinitionService.findAllPublishedExcludeCustomOrderByPopularity(SortOrder.DESCENDING);
    List<GadgetDefinition> allPubExCustomOrderedAsc =
        gadgetDefinitionService.findAllPublishedExcludeCustomOrderByPopularity(SortOrder.ASCENDING);
    assertEquals("3 published not custom", allPubExCustom.size(), 3);
    assertEquals("3 published not custom Ascending order", allPubExCustomOrderedAsc.size(), 3);
    assertEquals("First inserted install count", 3, allPubDefs.get(0).getInstallCount());
    assertEquals("Highest install count", 12, allPubExCustom.get(0).getInstallCount());
    assertEquals("Lowest install count", 1, allPubExCustomOrderedAsc.get(0).getInstallCount());
  }


  private GadgetDefinition addDef(GadgetDefinitionStatus status) {
    GadgetDefinition def = new GadgetDefinition();
    def.setStatus(status);
    gadgetDefinitionService.saveOrUpdate(def);
    return def;
  }
}
