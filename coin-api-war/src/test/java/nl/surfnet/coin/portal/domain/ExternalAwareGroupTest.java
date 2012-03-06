/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nl.surfnet.coin.portal.domain;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opensocial.models.Group;

/**
 * 
 *
 */
public class ExternalAwareGroupTest {

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.domain.ExternalAwareGroup#getTitle()}.
   */
  @Test
  public void testGetTitle() {
    Group group = new Group();
    group.setField("id", "nl:surfnet:diensten:surfnetgroup");
    group.setField("title", "SURFnet Group");

    ExternalAwareGroup externalAwareGroup = new ExternalAwareGroup(group);
    assertEquals(externalAwareGroup.getTitle(), externalAwareGroup.getTitle());

    group = new Group();
    group.setField("id", "urn:collab:group:hz.nl:tobeignored:more");
    group.setField("title", "External Group");
    externalAwareGroup = new ExternalAwareGroup(group);
    assertEquals("External Group (hz.nl)", externalAwareGroup.getTitle());

    group = new Group();
    group.setField("id", "urn:collab:group:hz.nl");
    group.setField("title", "External Group");
    externalAwareGroup = new ExternalAwareGroup(group);
    assertEquals("External Group (hz.nl)", externalAwareGroup.getTitle());

  }

}
