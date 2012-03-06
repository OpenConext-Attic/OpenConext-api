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

package nl.surfnet.coin.portal.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import nl.surfnet.coin.mock.AbstractMockHttpServerTest;
import nl.surfnet.coin.portal.domain.Gadget;
import nl.surfnet.coin.portal.domain.GadgetDefinition;
import nl.surfnet.coin.portal.util.CoinEnvironment;

import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;


/**
 * {@link Test} for {@link MetadataProviderImpl}
 * 
 */
public class MetadataProviderImplTest extends AbstractMockHttpServerTest {

  private static final String METADATA_JSON = "metadata.json";

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.service.impl.MetadataProviderImpl#getMetaData(java.util.List)} .
   */
  @Test
  public void testGetMetaData() {
    CoinEnvironment environment = new CoinEnvironment();
    // on this address listens the MockHttpServer
    environment.setMetadataUrl("http://localhost:8088/");

    MetadataProviderImpl provider = new MetadataProviderImpl();
    provider.setEnvironment(environment);
    // mock the response
    setResponseResource(new ByteArrayResource(METADATA_JSON.getBytes()));

    String metaData = provider.getMetaData(Collections
        .singletonList(createGadget()));
    assertEquals(METADATA_JSON, metaData);
  }

  private Gadget createGadget() {
    Gadget gadget = new Gadget();
    gadget.setDefinition(new GadgetDefinition());
    gadget.setId(1L);
    return gadget;
  }

}
