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
package nl.surfnet.coin.api.client.domain;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nl.surfnet.coin.api.client.domain.Email.Type;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

/**
 * PersonTest.java
 *
 */
public class PersonTest {

  /**
   * Test method for {@link nl.surfnet.coin.api.client.domain.Person#getId()}.
   */
  @SuppressWarnings("resource")
  @Test
  public void test_get_id_after_serialization() throws Exception {
    Person person = new Person();
    person.setId("id");
    
    new ObjectOutputStream(new FileOutputStream("target/person.ser")).writeObject(person);
    Person persistentPerson = (Person) new ObjectInputStream(new FileInputStream("target/person.ser")).readObject();
    
    assertEquals("id",persistentPerson.getId());
  }
  
  @Test
  public void testGetEmail() {
    Person person = new Person();
    assertNull(person.getEmailValue());
    
    person.addEmail(new Email("value@test.org", Type.email));
    assertEquals("value@test.org", person.getEmailValue());
    
  }

  @Test
  public void skipConvenienceMethodsOnSerialize() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper()
      .enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

    Person person = new Person();
    person.addEmail(new Email("value@test.org", Type.email));

    String serialized = objectMapper.writeValueAsString(person);
    assertThat("serialized Person should not include convenience getters", serialized, not(containsString("emailValue")));
  }
}
