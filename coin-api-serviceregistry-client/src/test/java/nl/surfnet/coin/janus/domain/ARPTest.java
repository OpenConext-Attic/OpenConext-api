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

package nl.surfnet.coin.janus.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link ARP}
 */
public class ARPTest {
  @Test
  public void testFromRestResponse() throws Exception {
    String all = "*";
    List<Object> attributeValues = new ArrayList<Object>();
    attributeValues.add(all);

    Map<String, List<Object>> attributes = new LinkedHashMap<String, List<Object>>();
    attributes.put("uuid", attributeValues);
    attributes.put("displayName", attributeValues);
    attributes.put("email", attributeValues);

    Map<String, Object> restResponse = new LinkedHashMap<String, Object>();
    final String name = "ARP_123";
    restResponse.put("name", name);
    final String description = "Attribute release policy 123";
    restResponse.put("description", description);
    restResponse.put("attributes", attributes);

    ARP arp = ARP.fromRestResponse(restResponse);
    assertEquals(name, arp.getName());
    assertEquals(description, arp.getDescription());
    assertEquals(3, arp.getAttributes().size());

    assertFalse(arp.isNoArp());
    assertFalse(arp.isNoAttrArp());
  }

  @Test
  public void testFromRestResponseNoAttributes() {
    Map<String, Object> restResponse = new LinkedHashMap<String, Object>();
    final String name = "ARP_123";
    restResponse.put("name", name);
    final String description = "Attribute release policy 123";
    restResponse.put("description", description);
    restResponse.put("attributes", null);
    ARP arp = ARP.fromRestResponse(restResponse);
    assertEquals(name, arp.getName());
    assertEquals(description, arp.getDescription());
    assertEquals(0, arp.getAttributes().size());
    assertTrue(arp.isNoAttrArp());
  }

  @Test
  public void fromRestResponseNoArp() {
    // When there is no arp at all, the REST response will be an empty map.
    Map<String, Object> restResponse = new HashMap<String, Object>();
    ARP arp = ARP.fromRestResponse(restResponse);
    assertTrue(arp.isNoArp());
  }
}
