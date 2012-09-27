/*
 Copyright 2012 SURFnet bv, The Netherlands

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package nl.surfnet.coin.db;

import com.googlecode.flyway.core.Flyway;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This unit test runs the Flyway migrations to see whether they are sane.
 */
public class MigrationsTest {

  @Test
  public void migrateHsqldb() {
    Flyway flyway = new Flyway();

    BasicDataSource ds = hsqldbDataSource();

    flyway.setBaseDir("db/migration/hsqldb");
    flyway.setDataSource(ds);
    flyway.migrate();
  }

  /**
   * This test is @Ignore'd as it requires a working MySQL instance somewhere.
   * Refer to {@link #mysqlDataSource()} if you want to run this against some MySQL instance.
   */
  @Test
  @Ignore
  public void migrateMysql() {
    Flyway flyway = new Flyway();

    BasicDataSource ds = mysqlDataSource();

    flyway.setBaseDir("db/migration/mysql");
    flyway.setDataSource(ds);
    flyway.migrate();

  }


  private BasicDataSource hsqldbDataSource() {
    BasicDataSource ds = new BasicDataSource();
    ds.setDriverClassName("org.hsqldb.jdbcDriver");
    ds.setUrl("jdbc:hsqldb:mem:api");
    ds.setUsername("sa");
    ds.setPassword("");
    return ds;
  }

  private BasicDataSource mysqlDataSource() {
    BasicDataSource ds = new BasicDataSource();
    ds.setDriverClassName("com.mysql.jdbc.Driver");
    ds.setUrl("jdbc:mysql://localhost:3306/apitest");
    ds.setUsername("root");
    ds.setPassword("");
    return ds;
  }
}
