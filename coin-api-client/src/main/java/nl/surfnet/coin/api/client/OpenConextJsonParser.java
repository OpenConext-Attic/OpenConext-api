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

import java.io.InputStream;

import nl.surfnet.coin.api.client.domain.AbstractEntry;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupEntry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.PersonEntry;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Parser for VOOT based json objects
 *
 */
public class OpenConextJsonParser {

  private ObjectMapper objectMapper = new ObjectMapper();

  public GroupMembersEntry parseTeamMembers(InputStream in) {
    return (GroupMembersEntry) parse(in, GroupMembersEntry.class);
  }

  public PersonEntry parsePerson(InputStream in) {
    return (PersonEntry) parse(in, PersonEntry.class);
  }

  public GroupEntry parseGroups(InputStream in) {
    return (GroupEntry) parse(in, GroupEntry.class);
  }

  public Group20Entry parseGroups20(InputStream in) {
    return (Group20Entry) parse(in, Group20Entry.class);
  }

  private Object parse(InputStream in, Class<? extends AbstractEntry> entry) {
    try {
      return objectMapper.readValue(in, entry);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
