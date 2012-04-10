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

package nl.surfnet.coin.api.oauth;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenImpl;
import org.springframework.security.oauth.provider.token.RandomValueProviderTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Token store for Oauth1 tokens.
 *
 */
@Component(value = "oauth1TokenServices")
public class OpenConextOauth1TokenServices extends RandomValueProviderTokenServices implements InitializingBean {

  Logger LOG = LoggerFactory.getLogger(OpenConextOauth1TokenServices.class);
  private JdbcTemplate jdbcTemplate;

  private final static String selectTokenFromAuthenticationSql = "select * from oauth1_tokens where token like ?";
  private final static String insertAccessTokenSql = "insert into oauth1_tokens values (?, ?, ?, ?, ?, ?, ?)";
  private final static String deleteTokenSql = "delete from oauth1_tokens where token like ?";

  public static class OAuthProviderTokenRowMapper implements RowMapper<OAuthProviderTokenImpl> {
    public OAuthProviderTokenImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
      OAuthProviderTokenImpl token = new OAuthProviderTokenImpl();
      token.setValue(rs.getString("token"));
      token.setCallbackUrl(rs.getString("callbackUrl"));
      token.setVerifier(rs.getString("verifier"));
      token.setSecret(rs.getString("secret"));
      token.setConsumerKey(rs.getString("consumerKey"));
      token.setAccessToken(rs.getBoolean("isAccessToken"));
      token.setTimestamp(rs.getLong("tokenTimestamp"));
      return token;
    }
  }

  @Autowired
  private DataSource dataSource;

  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();

    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  protected OAuthProviderTokenImpl readToken(String authentication) {
    OAuthProviderTokenImpl token;
    try {
      token = jdbcTemplate.queryForObject(selectTokenFromAuthenticationSql, new OAuthProviderTokenRowMapper(), authentication);
    } catch (EmptyResultDataAccessException e) {
      if (LOG.isInfoEnabled()) {
        LOG.debug("Failed to find token for input '{}'", authentication);
      }
      return null;
    }
    return token;
  }

  @Override
  protected void storeToken(String value, OAuthProviderTokenImpl token) {
    Assert.notNull(token, "Token cannot be null");
    Assert.notNull(value, "token value cannot be null");
    jdbcTemplate.update(insertAccessTokenSql, value,
        token.getCallbackUrl(),
        token.getVerifier(),
        token.getSecret(),
        token.getConsumerKey(),
        token.isAccessToken(),
        token.getTimestamp());
  }

  @Override
  protected OAuthProviderTokenImpl removeToken(String tokenValue) {
    final OAuthProviderTokenImpl token = readToken(tokenValue);
    jdbcTemplate.update(deleteTokenSql, tokenValue);
    return token;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

}
