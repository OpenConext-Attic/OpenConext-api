package nl.surfnet.coin.api.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static nl.surfnet.coin.api.service.GroupProviderAcl.GroupId.groupId;
import static nl.surfnet.coin.api.service.GroupProviderAcl.ServiceProviderId.spId;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:coin-api-properties-context.xml", "classpath:coin-api-groupzy.xml"})
@ActiveProfiles("groupzy")
public class JdbcGroupServiceProviderAclTest {

  @Autowired
  @Qualifier("groupZyJdbcTemplate")
  JdbcTemplate groupZyJdbcTemplate;

  private JdbcGroupServiceProviderAcl serviceProviderAcl;

  @Before
  public void setUp() throws Exception {
    serviceProviderAcl = new JdbcGroupServiceProviderAcl(groupZyJdbcTemplate);
  }

  @Test
  public void testNoAccessToGroupWhenNoAcl() throws Exception {
    boolean access = serviceProviderAcl.hasAccessTo(spId("id"), groupId("groupId"));
    assertFalse(access);
  }

  @Test
  public void testAccessToGroupWhenInAcl() throws Exception {
    String spEntityId = UUID.randomUUID().toString();
    String teamId = UUID.randomUUID().toString();
    groupZyJdbcTemplate.update(
      "insert into service_provider_group (sp_entity_id, team_id, created_at, updated_at) values (?, ?, ?, ?)",
      spEntityId,
      teamId,
      new DateTime().toDate(),
      new DateTime().toDate()
    );

    boolean access = serviceProviderAcl.hasAccessTo(spId(spEntityId), groupId(teamId));
    assertTrue(access);
  }
}
