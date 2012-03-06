/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
* 
* http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/


package nl.surfnet.coin.portal.control;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import nl.surfnet.coin.portal.domain.IdentityProvider;

import org.junit.Test;

public class IdentityProviderComparatorTest {

  @Test
  public void testCompare() {
    List<IdentityProvider> idps = new ArrayList<IdentityProvider>();
    idps.add(constructIdentityProvider("c"));
    idps.add(constructIdentityProvider("a"));
    idps.add(constructIdentityProvider("b"));
    Collections.sort(idps, new IdentityProviderComparator());

    assertEquals("a", idps.get(0).getDisplayName("en"));
    assertEquals("b", idps.get(1).getDisplayName("en"));
    assertEquals("c", idps.get(2).getDisplayName("en"));
  }

  private IdentityProvider constructIdentityProvider(String displayName) {
    IdentityProvider ip1 = new IdentityProvider();
    ip1.setDisplayName("en",displayName);
    return ip1;
  }

}
