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

import javax.annotation.Resource;
import javax.sql.DataSource;

import nl.surfnet.coin.api.shib.ShibbolethAuthenticationToken;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenImpl;
import org.springframework.security.oauth.provider.token.RandomValueProviderTokenServices;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Token store for Oauth1 tokens.
 */
@Component(value = "oauth1TokenServices")
public class OpenConextOauth1TokenServices extends RandomValueProviderTokenServices implements InitializingBean {

  Logger LOG = LoggerFactory.getLogger(OpenConextOauth1TokenServices.class);
  private JdbcTemplate jdbcTemplate;

  private final static String selectTokenSql = "select * from oauth1_tokens where token like ?";
  private final static String insertTokenSql = "insert into oauth1_tokens values (?, ?, ?, ?, ?, ?, ?, ?)";
  private final static String deleteTokenSql = "delete from oauth1_tokens where token like ?";

  @Resource(name="janusClientDetailsService")
  private ConsumerDetailsService consumerDetailsService;

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
      token.setUserAuthentication((Authentication) SerializationUtils.deserialize(rs.getBlob("userAuthentication")
          .getBinaryStream()));
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
      token = jdbcTemplate.queryForObject(selectTokenSql, new OAuthProviderTokenRowMapper(), authentication);
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
    Authentication userAuthentication = token.getUserAuthentication();
    if (token.isAccessToken()) {
      String consumerKey = token.getConsumerKey();
      /*
       * get the client detail from Janus as we are unable to store them
       * somewhere along the 'road' and we cache this call anyway
       */
      ConsumerDetails consumerDetails = consumerDetailsService.loadConsumerByConsumerKey(consumerKey);
      if (consumerDetails instanceof OpenConextConsumerDetails) {
        OpenConextConsumerDetails extendedBaseConsumerDetails = (OpenConextConsumerDetails) consumerDetails;
        if (userAuthentication instanceof PreAuthenticatedAuthenticationToken) {
          PreAuthenticatedAuthenticationToken pre = (PreAuthenticatedAuthenticationToken) userAuthentication;
          Object principal = pre.getPrincipal();
          if (principal instanceof ClientMetaDataUser) {
            ((ClientMetaDataUser) principal).setClientMetaData(extendedBaseConsumerDetails.getClientMetaData());
          } else if (principal instanceof ShibbolethAuthenticationToken) {
            ((ShibbolethAuthenticationToken) principal).setClientMetaData(extendedBaseConsumerDetails
                .getClientMetaData());
          } else {
            throw new RuntimeException("The principal on the PreAuthenticatedAuthenticationToken is of the type '"
                + (principal != null ? principal.getClass() : "null")
                + "'. Required is a (sub)class of ClientMetaDataUser or a (sub)class of ShibbolethAuthenticationToken");
          }
        } else if (userAuthentication instanceof ShibbolethAuthenticationToken) {
          ShibbolethAuthenticationToken shibToken = (ShibbolethAuthenticationToken) userAuthentication;
          shibToken.setClientMetaData(extendedBaseConsumerDetails.getClientMetaData());
          
        } else {
          throw new RuntimeException("The userAuthentication is of the type '"
              + (userAuthentication != null ? userAuthentication.getClass() : "null")
              + "'. Required is a (sub)class of PreAuthenticatedAuthenticationToken or ShibbolethAuthenticationToken");
        }
      } else {
        throw new RuntimeException("The consumerDetails is of the type '"
            + (consumerDetails != null ? consumerDetails.getClass() : "null")
            + "'. Required is a (sub)class of ExtendedBaseConsumerDetails");
      }
    }
    jdbcTemplate.update(deleteTokenSql, value);
    jdbcTemplate.update(insertTokenSql, value, token.getCallbackUrl(), token.getVerifier(), token.getSecret(),
        token.getConsumerKey(), token.isAccessToken(), token.getTimestamp(),
        SerializationUtils.serialize(userAuthentication));
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
