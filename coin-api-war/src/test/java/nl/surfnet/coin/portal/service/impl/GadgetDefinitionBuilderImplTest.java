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

import nl.surfnet.coin.portal.domain.Gadget;
import nl.surfnet.coin.portal.domain.GadgetDefinition;
import nl.surfnet.coin.portal.model.JsonGadgetSpec;
import nl.surfnet.coin.portal.service.MetadataProvider;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Test for {@link nl.surfnet.coin.portal.service.impl.GadgetDefinitionBuilderImpl}
 */
public class GadgetDefinitionBuilderImplTest {


  @Test
  public void returnGadgetSpec() throws Exception {

    GadgetDefinitionBuilderImpl definitionBuilder = new GadgetDefinitionBuilderImpl(workingMetadataProvider());
    JsonGadgetSpec gadgetSpec = definitionBuilder.getGadgetSpec(getWorkingMetadata());

    assertNotNull("GadgetSpec", gadgetSpec);
    assertEquals("Buienradar (Nieuw)", gadgetSpec.getTitle());
    assertEquals("JPRmedia", gadgetSpec.getAuthor());
    assertEquals("info@jprmedia.eu", gadgetSpec.getAuthorEmail());
    assertNull(gadgetSpec.getDescription());
    assertEquals("http://buienradar.jprmedia.net/buienradar_img_screen.png", gadgetSpec.getScreenshot());
    assertEquals("http://buienradar.jprmedia.net/buienradar_img_thumb.png", gadgetSpec.getThumbnail());
  }

  @Test
  public void returnGadgetDefinition() throws Exception {

    GadgetDefinitionBuilderImpl definitionBuilder = new GadgetDefinitionBuilderImpl(workingMetadataProvider());
    JsonGadgetSpec gadgetSpec = definitionBuilder.getGadgetSpec(getWorkingMetadata());
    GadgetDefinition definition = definitionBuilder.fillGadgetDefinitionFromSpec(gadgetSpec,
        "http://localhost/gadgetdefinition.xml");
    assertNotNull("GadgetDefinition", definition);
    assertEquals("Buienradar (Nieuw)", definition.getTitle());
    assertEquals("JPRmedia", definition.getAuthor());
    assertEquals("info@jprmedia.eu", definition.getAuthorEmail());
    assertNull(definition.getDescription());
    assertEquals("http://buienradar.jprmedia.net/buienradar_img_screen.png", definition.getScreenshot());
    assertEquals("http://buienradar.jprmedia.net/buienradar_img_thumb.png", definition.getThumbnail());
  }

  @Test
  public void returnNullOnErrors() throws Exception {
    GadgetDefinitionBuilderImpl builder = new GadgetDefinitionBuilderImpl(errorMetadataProvider());
    assertNull("No gadget definition", builder.build("http://localhost/notrunning.xml"));
  }

  private static MetadataProvider workingMetadataProvider() {
    return new MetadataProvider() {
      @Override
      public String getMetaData(List<Gadget> gadgets) {
        return getWorkingMetadata();
      }
    };
  }

  private static MetadataProvider errorMetadataProvider() {
    return new MetadataProvider() {
      @Override
      public String getMetaData(List<Gadget> gadgets)  {
        ClassPathResource resource = new ClassPathResource("jsons/mock-gadget-metadata-error.json");
        try {
          return IOUtils.toString(resource.getInputStream());
        } catch (IOException e) {
          throw new RuntimeException("Could not read jsons/mock-gadget-metadata-error.json from classpath");
        }
      }
    };
  }

  private static String getWorkingMetadata() {
    ClassPathResource resource = new ClassPathResource("jsons/mock-gadget-metadata.json");
    try {
      return IOUtils.toString(resource.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException("Could not read jsons/mock-gadget-metadata.json from classpath");
    }
  }


}
