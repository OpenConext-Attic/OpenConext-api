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

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenImpl;

import nl.surfnet.coin.api.shib.ShibbolethAuthenticationToken;
import nl.surfnet.coin.db.AbstractInMemoryDatabaseTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class OpenConextOauth1TokenServicesTest extends AbstractInMemoryDatabaseTest {


  private OpenConextOauth1TokenServices s;

  @Before
  public void initWithDb() throws Exception {
    s = new OpenConextOauth1TokenServices();
    s.setDataSource(getJdbcTemplate().getDataSource());
    s.afterPropertiesSet();
  }

  @Test
  public void readTokenNullInput() {
    assertNull(s.readToken(null));
  }

  @Test
  public void readTokenEmptyInput() {
    assertNull(s.readToken(""));
  }

  @Test
  public void storeNullToken() {
    try {
      s.storeToken(null, null);
      fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      s.storeToken("", null);
      fail();
    } catch (IllegalArgumentException e) {
    }
    try {
        s.storeToken(null, new OAuthProviderTokenImpl());
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void storeTokenHappy() {

    s.storeToken("footoken", buildToken());

    assertEquals("One token should be inserted",
        1, getJdbcTemplate().queryForInt("select count(*) from oauth1_tokens"));

    assertEquals("token id should match inserted one",
        "footoken", getJdbcTemplate().queryForObject("select * from oauth1_tokens where token = ?",
        new OpenConextOauth1TokenServices.OAuthProviderTokenRowMapper(), "footoken").getValue());
  }

  @Test
  public void removeTokenHappy() {
    s.storeToken("footoken", buildToken());
    s.removeToken("footoken");

    assertEquals("No tokens should be in store after removing the only one.",
        0, getJdbcTemplate().queryForInt("select count(*) from oauth1_tokens"));
  }

  private OAuthProviderTokenImpl buildToken() {
    final OAuthProviderTokenImpl token = new OAuthProviderTokenImpl();
    token.setValue("value");
    token.setVerifier("verifier");
    token.setSecret("ssh");
    token.setCallbackUrl("callbackurl");
    token.setConsumerKey("consumerkey");
    ShibbolethAuthenticationToken userAuthentication = new ShibbolethAuthenticationToken(Collections.EMPTY_LIST);
    userAuthentication.setClientMetaData(new JanusClientMetadata());
    token.setUserAuthentication(userAuthentication);
    return token;
  }

  /* (non-Javadoc)
   * @see nl.surfnet.coin.db.AbstractInMemoryDatabaseTest#getMockDataContentFilename()
   */
  @Override
  public String getMockDataContentFilename() {
    return "coin-api-test-db.sql";
  }

  /* (non-Javadoc)
   * @see nl.surfnet.coin.db.AbstractInMemoryDatabaseTest#getMockDataCleanUpFilename()
   */
  @Override
  public String getMockDataCleanUpFilename() {
    return "coin-api-test-db-cleanup.sql";
  }
}
