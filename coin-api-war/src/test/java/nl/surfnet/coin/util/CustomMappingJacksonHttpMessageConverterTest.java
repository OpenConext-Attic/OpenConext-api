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
package nl.surfnet.coin.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;

/**
 *
 *
 */
public class CustomMappingJacksonHttpMessageConverterTest {

  /**
   * Test method for
   * {@link nl.surfnet.coin.util.CustomMappingJacksonHttpMessageConverter#CustomMappingJacksonHttpMessageConverter()}
   * .
   */
  @Test
  public void testCustomMappingJacksonHttpMessageConverter() throws JsonGenerationException, JsonMappingException,
      IOException {
    CustomMappingJacksonHttpMessageConverter convert = new CustomMappingJacksonHttpMessageConverter();
    OutputStream out = new ByteArrayOutputStream();
    Person value = new Person();
    value.setError("error");
    convert.getObjectMapper().writeValue(out, value);
    assertEquals("{\"error\":\"error\"}", out.toString());
  }

}
