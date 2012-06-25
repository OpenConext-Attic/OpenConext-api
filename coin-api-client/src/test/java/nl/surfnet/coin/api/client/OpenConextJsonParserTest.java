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

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.Person;

import static junit.framework.Assert.assertEquals;

/**
 * Test for {@link OpenConextJsonParser}
 */
public class OpenConextJsonParserTest {
  private OpenConextJsonParser parser = new OpenConextJsonParser();

  @Test
  public void testParseResultWrapper() throws Exception {
    Group20Entry entry = parser.parseGroups20(new ClassPathResource("multiple-wrapped-groups20.json").getInputStream());
    assertEquals(3, entry.getEntry().size());
  }

  @Test
  public void testParseGroup20() throws Exception {
    final Group20Entry group20Entry = parser.parseGroups20(new ClassPathResource("multiple-groups20.json").getInputStream());
    assertEquals(3, group20Entry.getEntry().size());
  }
  
  @Test
  public void testNotNullInclusion() throws Exception {
    ObjectMapper objectMapper = parser.getObjectMapper();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    objectMapper.writeValue(  out, new Person());
    assertEquals("{}", out.toString());
    
  }
}
