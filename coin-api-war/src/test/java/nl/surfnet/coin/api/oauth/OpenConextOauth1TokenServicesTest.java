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

import java.io.IOException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.Assert.assertNull;

public class OpenConextOauth1TokenServicesTest {


  private OpenConextOauth1TokenServices s;

  @Before
  public void initWithDb() throws Exception {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setPassword("");
    dataSource.setUsername("sa");
    dataSource.setUrl("jdbc:hsqldb:mem:coin_api");
    dataSource.setDriverClassName("org.hsqldb.jdbcDriver");

    s = new OpenConextOauth1TokenServices();
    s.setDataSource(dataSource);
    new JdbcTemplate(dataSource).execute(FileUtils.readFileToString(new ClassPathResource("coin-api-test-db.sql").getFile()));
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
}
