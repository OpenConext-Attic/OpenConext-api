package nl.surfnet.coin.api.service;

import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcGroupServiceProviderAcl implements GroupProviderAcl {
  private final JdbcTemplate jdbcTemplate;

  public JdbcGroupServiceProviderAcl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public boolean hasAccessTo(ServiceProviderId serviceProviderId, GroupId groupId) {
    int count = jdbcTemplate.queryForInt("select count(id) from service_provider_group where sp_entity_id = ? and team_id = ?", serviceProviderId.id, groupId.id);
    return count > 0;
  }
}
