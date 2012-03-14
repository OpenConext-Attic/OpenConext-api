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
package nl.surfnet.coin.api.client;

import java.io.InputStream;
import java.util.List;

import nl.surfnet.coin.api.client.domain.AbstractEntry;
import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.GroupEntry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.domain.PersonEntry;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * 
 *
 */
@SuppressWarnings("unchecked")
public class OpenConextJsonParser {
  
  private ObjectMapper objectMapper = new ObjectMapper();
  
  public List<Person> parseTeamMembers(InputStream in) {
    return (List<Person>) parse(in, GroupMembersEntry.class);
  }
  
  public Person parsePerson(InputStream in) {
    return (Person) parse(in,PersonEntry.class);
  }
  
  public List<Group> parseGroups(InputStream in) {
    return (List<Group>) parse(in,GroupEntry.class);
  }
  
  private Object parse(InputStream in, Class<? extends AbstractEntry> entry) {
    AbstractEntry result;
    try {
      result = objectMapper.readValue(in, entry);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return result.getResult();
  }
  
}
