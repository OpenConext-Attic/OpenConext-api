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

package nl.surfnet.coin.teams.service.impl;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.surfnet.coin.db.AbstractInMemoryDatabaseTest;
import nl.surfnet.coin.teams.domain.ExternalGroup;
import nl.surfnet.coin.teams.domain.TeamExternalGroup;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;


/**
 * Test for {@link TeamExternalGroupDaoImplTest}
 */
public class TeamExternalGroupDaoImplTest extends AbstractInMemoryDatabaseTest {

  private static TeamExternalGroupDaoImpl teamExternalGroupDao;
  private static final String AVANS_GROUP_IDENT = "urn:collab:group:avans.nl:nl.avans.avans-employee_grp";
  private static final String HZ_GROUP_IDENT = "urn:collab:group:hz.nl:nl.hz.hz-1234";
  private static final String TEAM_JASHA = "nl:surfnet:diensten:team_jasha";
  private static final String TEAM_OKKE = "nl:surfnet:diensten:team_okke";
  private static final String NON_EXISTING_EXTERNAL_GROUP_IDENT = "urn:collab:group:foo.nl:nl.foo.bar";

  @Before
  public void setUp() throws Exception {
    teamExternalGroupDao = new TeamExternalGroupDaoImpl();
    teamExternalGroupDao.setJdbcTemplate(super.getJdbcTemplate());
  }


  @Test
  public void testGetExternalGroupByIdentifier() throws Exception {
    String identifier = AVANS_GROUP_IDENT;
    final ExternalGroup group = teamExternalGroupDao.getExternalGroupByIdentifier(identifier);

    assertNotNull(group);
    assertEquals(identifier, group.getIdentifier());
    assertEquals("avans", group.getGroupProviderIdentifier());
  }

  @Test
  public void testGetExternalGroupByIdentifier_DoesNotExist() throws Exception {
    String identifier = NON_EXISTING_EXTERNAL_GROUP_IDENT;
    final ExternalGroup group = teamExternalGroupDao.getExternalGroupByIdentifier(identifier);

    assertNull(group);
  }

  @Test
  public void testGetByTeamIdentifierAndExternalGroupIdentifier() throws Exception {
    String teamId = TEAM_OKKE;
    String externalGroupIdentifier = HZ_GROUP_IDENT;

    final TeamExternalGroup teamExternalGroup =
        teamExternalGroupDao.getByTeamIdentifierAndExternalGroupIdentifier(teamId, externalGroupIdentifier);
    assertNotNull(teamExternalGroup);
    assertEquals(externalGroupIdentifier, teamExternalGroup.getExternalGroup().getIdentifier());
  }

  @Test
  public void testGetByTeamIdentifierAndExternalGroupIdentifier_NotFound() throws Exception {
    String teamId = TEAM_JASHA;
    String externalGroupIdentifier = HZ_GROUP_IDENT;

    final TeamExternalGroup teamExternalGroup =
        teamExternalGroupDao.getByTeamIdentifierAndExternalGroupIdentifier(teamId, externalGroupIdentifier);
    assertNull(teamExternalGroup);
  }

  @Test
  public void testGetByTeamIdentifier() throws Exception {
    String teamId = TEAM_JASHA;
    final List<TeamExternalGroup> teamExternalGroups = teamExternalGroupDao.getByTeamIdentifier(teamId);

    assertFalse(teamExternalGroups.isEmpty());

    final TeamExternalGroup teamExternalGroup = teamExternalGroups.get(0);
    assertTrue(teamExternalGroup.getId() == 1001L);

    final ExternalGroup externalGroup = teamExternalGroup.getExternalGroup();
    assertTrue(externalGroup.getId() == 1L);
    assertEquals(AVANS_GROUP_IDENT, externalGroup.getIdentifier());
  }

  @Test
  public void testGetByTeamIdentifier_emptyList() throws Exception {
    String teamId = "nl:surfnet:diensten:team_stein";
    final List<TeamExternalGroup> teamExternalGroups = teamExternalGroupDao.getByTeamIdentifier(teamId);

    assertTrue(teamExternalGroups.isEmpty());
  }

  @Test
  public void testSaveOrUpdate_ExistingCombination() throws Exception {
    ExternalGroup newEg = new ExternalGroup();
    newEg.setIdentifier(AVANS_GROUP_IDENT);
    final String newName = "Avans-Test groep";
    final String newDesc = "Avans Test groep";
    newEg.setName(newName);
    newEg.setDescription(newDesc);

    TeamExternalGroup newTeg = new TeamExternalGroup();
    newTeg.setGrouperTeamId(TEAM_JASHA);
    newTeg.setExternalGroup(newEg);

    TeamExternalGroup teamExternalGroup = teamExternalGroupDao.getByTeamIdentifierAndExternalGroupIdentifier(TEAM_JASHA, AVANS_GROUP_IDENT);
    assertTrue(teamExternalGroup.getId() == 1001L);
    ExternalGroup externalGroup = teamExternalGroup.getExternalGroup();
    final Long externalGroupId = externalGroup.getId();
    assertEquals("avans-employee_grp", externalGroup.getName());
    assertEquals("avans test groep", externalGroup.getDescription());

    teamExternalGroupDao.saveOrUpdate(newTeg);

    teamExternalGroup = teamExternalGroupDao.getByTeamIdentifierAndExternalGroupIdentifier(TEAM_JASHA,
        AVANS_GROUP_IDENT);
    assertTrue(teamExternalGroup.getId() == 1001L);
    externalGroup = teamExternalGroup.getExternalGroup();
    assertEquals(externalGroupId, externalGroup.getId());
    assertEquals(newName, externalGroup.getName());
    assertEquals(newDesc, externalGroup.getDescription());
  }

  @Test
  public void testSaveOrUpdate_ExistingExternalGroupNewCombination() throws Exception {
    ExternalGroup newEg = new ExternalGroup();
    newEg.setIdentifier(HZ_GROUP_IDENT);
    final String newName = "HZ-Test groep";
    final String newDesc = "HZ Test groep";
    newEg.setName(newName);
    newEg.setDescription(newDesc);

    TeamExternalGroup newTeg = new TeamExternalGroup();
    newTeg.setGrouperTeamId(TEAM_JASHA);
    newTeg.setExternalGroup(newEg);

    TeamExternalGroup teamExternalGroup = teamExternalGroupDao.getByTeamIdentifierAndExternalGroupIdentifier(TEAM_JASHA,
        HZ_GROUP_IDENT);
    assertNull(teamExternalGroup);

    ExternalGroup externalGroup = teamExternalGroupDao.getExternalGroupByIdentifier(HZ_GROUP_IDENT);
    assertNotNull(externalGroup);
    final Long externalGroupId = externalGroup.getId();

    teamExternalGroupDao.saveOrUpdate(newTeg);

    teamExternalGroup = teamExternalGroupDao.getByTeamIdentifierAndExternalGroupIdentifier(TEAM_JASHA, HZ_GROUP_IDENT);
    assertNotNull(teamExternalGroup.getId());
    externalGroup = teamExternalGroup.getExternalGroup();
    assertEquals(externalGroupId, externalGroup.getId());
    assertEquals(newName, externalGroup.getName());
    assertEquals(newDesc, externalGroup.getDescription());
  }

  @Test
  public void testSaveOrUpdate_New() throws Exception {
    ExternalGroup newEg = new ExternalGroup();
    newEg.setIdentifier(NON_EXISTING_EXTERNAL_GROUP_IDENT);
    final String newName = "FOOBAR-Test groep";
    final String newDesc = "FOOBAR Test groep";
    newEg.setName(newName);
    newEg.setDescription(newDesc);

    TeamExternalGroup newTeg = new TeamExternalGroup();
    String teamFooBar = "nl:surfnet:diensten:foobar";
    newTeg.setGrouperTeamId(teamFooBar);
    newTeg.setExternalGroup(newEg);

    TeamExternalGroup teamExternalGroup = teamExternalGroupDao.getByTeamIdentifierAndExternalGroupIdentifier(teamFooBar,
        NON_EXISTING_EXTERNAL_GROUP_IDENT);
    assertNull(teamExternalGroup);

    ExternalGroup externalGroup = teamExternalGroupDao.getExternalGroupByIdentifier(NON_EXISTING_EXTERNAL_GROUP_IDENT);
    assertNull(externalGroup);

    teamExternalGroupDao.saveOrUpdate(newTeg);

    teamExternalGroup = teamExternalGroupDao.getByTeamIdentifierAndExternalGroupIdentifier(teamFooBar,
        NON_EXISTING_EXTERNAL_GROUP_IDENT);
    assertNotNull(teamExternalGroup.getId());
    externalGroup = teamExternalGroup.getExternalGroup();
    assertNotNull(externalGroup.getId());
    assertEquals(newName, externalGroup.getName());
    assertEquals(newDesc, externalGroup.getDescription());
  }


  @Test
  public void testDelete_groupHasMultipleLinks() throws Exception {
    TeamExternalGroup teamExternalGroup =
        teamExternalGroupDao.getByTeamIdentifierAndExternalGroupIdentifier(TEAM_JASHA, AVANS_GROUP_IDENT);
    ExternalGroup externalGroup = teamExternalGroupDao.getExternalGroupByIdentifier(AVANS_GROUP_IDENT);

    assertNotNull(teamExternalGroup);
    assertNotNull(externalGroup);

    teamExternalGroupDao.delete(teamExternalGroup);

    teamExternalGroup =
            teamExternalGroupDao.getByTeamIdentifierAndExternalGroupIdentifier(TEAM_JASHA, AVANS_GROUP_IDENT);
    assertNull(teamExternalGroup);

    externalGroup = teamExternalGroupDao.getExternalGroupByIdentifier(AVANS_GROUP_IDENT);
    assertNotNull(externalGroup);
  }

  @Test
  public void testDelete_groupHasSingleLinks() throws Exception {
    TeamExternalGroup teamExternalGroup =
        teamExternalGroupDao.getByTeamIdentifierAndExternalGroupIdentifier(TEAM_OKKE, HZ_GROUP_IDENT);
    ExternalGroup externalGroup = teamExternalGroupDao.getExternalGroupByIdentifier(HZ_GROUP_IDENT);

    assertNotNull(teamExternalGroup);
    assertNotNull(externalGroup);

    teamExternalGroupDao.delete(teamExternalGroup);

    teamExternalGroup =
            teamExternalGroupDao.getByTeamIdentifierAndExternalGroupIdentifier(TEAM_OKKE, HZ_GROUP_IDENT);
    assertNull(teamExternalGroup);

    externalGroup = teamExternalGroupDao.getExternalGroupByIdentifier(HZ_GROUP_IDENT);
    assertNull(externalGroup);
  }

  @Test
  public void testDelete_LinkToDeleteFails() {
    String orphanGroup = "urn:collab:group:hz.nl:nl.hz.hz-2345";
    ExternalGroup externalGroup = teamExternalGroupDao.getExternalGroupByIdentifier(orphanGroup);

    TeamExternalGroup teg = new TeamExternalGroup();
    teg.setId(12345L);
    teg.setGrouperTeamId("this.id.is.fake");
    teg.setExternalGroup(externalGroup);

    teamExternalGroupDao.delete(teg);

    externalGroup = teamExternalGroupDao.getExternalGroupByIdentifier(orphanGroup);
    assertNotNull(externalGroup);
  }

  @Override
  public String getMockDataContentFilename() {
    return "test-data-external-groups.sql";
  }

  @Override
  public String getMockDataCleanUpFilename() {
    return "cleanup-test-data-external-groups.sql";
  }
}
