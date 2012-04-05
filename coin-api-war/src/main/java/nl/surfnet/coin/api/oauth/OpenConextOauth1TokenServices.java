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
import java.sql.Types;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth.provider.token.OAuthAccessProviderToken;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenImpl;
import org.springframework.security.oauth.provider.token.RandomValueProviderTokenServices;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.util.Assert;

public class OpenConextOauth1TokenServices extends RandomValueProviderTokenServices {

  Logger LOG = LoggerFactory.getLogger(OpenConextOauth1TokenServices.class);
  private final JdbcTemplate jdbcTemplate;

  private String selectTokenFromAuthenticationSql;
  private String insertAccessTokenSql;

  public OpenConextOauth1TokenServices(DataSource dataSource) {
    Assert.notNull(dataSource, "DataSource required");
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  protected OAuthProviderTokenImpl readToken(String authentication) {
    OAuthProviderTokenImpl token = null;
    try {
      token = jdbcTemplate.queryForObject(selectTokenFromAuthenticationSql,
          new RowMapper<OAuthProviderTokenImpl>() {
            public OAuthProviderTokenImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
              OAuthProviderTokenImpl token = new OAuthProviderTokenImpl();
              token.setCallbackUrl(rs.getString(1));
              // TODO: other columns
              return token;
            }
          }, authentication);
    } catch (EmptyResultDataAccessException e) {
      if (LOG.isInfoEnabled()) {
        LOG.debug("Failed to find token for input '{}'", authentication);
      }
      return null;
    }
    return token;
  }

  @Override
  protected void storeToken(String tokenValue, OAuthProviderTokenImpl token) {
//    jdbcTemplate.update(
//        insertAccessTokenSql,
//        new Object[] { token.getValue(), new SqlLobValue(SerializationUtils.serialize(token)),
//            tokenValue,
//            new SqlLobValue(SerializationUtils.serialize(authentication)), refreshToken }, new int[] {
//        Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.BLOB, Types.VARCHAR });
  }

  @Override
  protected OAuthProviderTokenImpl removeToken(String tokenValue) {
    return null;
  }
}
