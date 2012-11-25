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

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

/**
 * PersonTest.java
 *
 */
public class PersonTest {

  /**
   * Test method for {@link nl.surfnet.coin.api.client.domain.Person#getId()}.
   */
  @Test
  public void test_get_id_after_serialization() throws Exception {
    Person person = new Person();
    person.setId("id");
    
    new ObjectOutputStream(new FileOutputStream("target/person.ser")).writeObject(person);
    Person persistentPerson = (Person) new ObjectInputStream(new FileInputStream("target/person.ser")).readObject();
    
    assertEquals("id",persistentPerson.getId());
  }

}
