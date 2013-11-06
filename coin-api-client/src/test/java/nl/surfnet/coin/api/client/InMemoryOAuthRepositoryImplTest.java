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

package nl.surfnet.coin.api.client;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InMemoryOAuthRepositoryImplTest {

  private InMemoryOAuthRepositoryImpl repository;

  @Before
  public void before() {
    repository =  new InMemoryOAuthRepositoryImpl();
  }

  @Test
  public void store() {
    repository.storeToken("", "user");
  }

  @Test
  public void retrieve() {
    String token = "";
    repository.storeToken(token, "user");
    String token1 = repository.getToken("user");
    assertEquals(token, token1);
  }

  @Test
  public void remove() {
    repository.storeToken("foobar", "blaat");
    repository.removeToken("blaat");

    repository.removeToken(null);
  }
}
