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

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import nl.surfnet.coin.api.client.domain.Group20;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyVararg;
import static org.mockito.Mockito.when;

public class ApiGrouperDaoImplTest {

  @InjectMocks
  private ApiGrouperDao dao;

  @Mock
  private JdbcTemplate jdbcTemplate;

  @Before
  public void init() throws IOException {
    dao = new ApiGrouperDaoImpl();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getGroupsEmpty() {
    when(
        jdbcTemplate.query(anyString(), (Object[]) anyVararg(),
            (ApiGrouperDaoImpl.OpenSocial20GroupRowMapper) anyObject())).thenReturn(new ArrayList<Group20>());
    assertEquals(0, dao.findAllGroup20sByMember("personid", 1, 1, "id").getEntry().size());
  }

  @Test
  public void getGroups() {
    final ArrayList<Group20> group20s = new ArrayList<Group20>();
    group20s.add(new Group20("foo", "bar", "baz"));
    when(
        jdbcTemplate.query(anyString(), (Object[]) anyVararg(),
            (ApiGrouperDaoImpl.OpenSocial20GroupRowMapper) anyObject())).thenReturn(group20s);
    assertEquals(1, dao.findAllGroup20sByMember("personid", 1, 1, "id").getEntry().size());
  }

  @Test
  public void sortByOptions() {
    ApiGrouperDaoImpl daoImpl = new ApiGrouperDaoImpl();
    String sql = daoImpl.formatSQLWithSortByOption(null);
    assertTrue(sql.endsWith("order by gg.name limit ? offset ?"));
    try {
      daoImpl.formatSQLWithSortByOption("wtf");
      fail();
    } catch (RuntimeException e) {
    }
    sql = daoImpl.formatSQLWithSortByOption("title");
    assertTrue(sql.endsWith("order by gg.display_name limit ? offset ?"));

  }
}
