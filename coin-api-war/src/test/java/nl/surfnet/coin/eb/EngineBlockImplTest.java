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
package nl.surfnet.coin.eb;

import nl.surfnet.coin.db.AbstractInMemoryDatabaseTest;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import static junit.framework.Assert.assertEquals;

/**
 * 
 *
 */
public class EngineBlockImplTest extends AbstractInMemoryDatabaseTest {

  private EngineBlockImpl engineBlock;

  /**
   * Test method for
   * {@link nl.surfnet.coin.eb.EngineBlockImpl#getUserUUID(java.lang.String)} .
   */
  @Test
  public void testGetPersistentNameIdentifier() {
    // user_uuid
    initEngineBlock();
    String persistentNameIdentifier = engineBlock.getUserUUID("persistent");
    assertEquals("user_uuid", persistentNameIdentifier);

  }

  private void initEngineBlock() {
    engineBlock = new EngineBlockImpl();
    engineBlock.setEbJdbcTemplate(getJdbcTemplate());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.db.AbstractInMemoryDatabaseTest#getMockDataContentFilename
   * ()
   */
  @Override
  public String getMockDataContentFilename() {
    return "sql/test-data-eb.sql";
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.db.AbstractInMemoryDatabaseTest#getMockDataCleanUpFilename
   * ()
   */
  @Override
  public String getMockDataCleanUpFilename() {
    return "sql/cleanup-test-data-eb.sql";
  }

}
