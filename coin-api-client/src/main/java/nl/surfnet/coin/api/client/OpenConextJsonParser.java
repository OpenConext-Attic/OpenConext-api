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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;

import nl.surfnet.coin.api.client.domain.AbstractEntry;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupEntry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.api.client.domain.ResultWrapper;

/**
 * Parser for VOOT based json objects
 * 
 */
public class OpenConextJsonParser {

  private ObjectMapper objectMapper;

  public OpenConextJsonParser() {
    objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  }

  public GroupMembersEntry parseTeamMembers(InputStream in) {
    GroupMembersEntry result;
    try {
      String json = IOUtils.toString(in);
      InputStream stream = new ByteArrayInputStream(json.getBytes());
      final JsonNode jsonNodes = objectMapper.readTree(json);
      if (jsonNodes.has("result")) {
        result = parseTeamMembersResultWrapper(stream).getResult();
      } else if (jsonNodes.has("entry")) {
        result = (GroupMembersEntry) parse(stream, GroupMembersEntry.class);
      } else {
        throw new RuntimeException("Unrecognized JSON " + json);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  public PersonEntry parsePerson(InputStream in) {
    return (PersonEntry) parse(in, PersonEntry.class);
  }

  public GroupEntry parseGroups(InputStream in) {
    return (GroupEntry) parse(in, GroupEntry.class);
  }

  public Group20Entry parseGroups20(InputStream in) {
    Group20Entry result;
    try {
      String json = IOUtils.toString(in);
      InputStream stream = new ByteArrayInputStream(json.getBytes());
      final JsonNode jsonNodes = objectMapper.readTree(json);
      if (jsonNodes.has("result")) {
        result = parseGroup20ResultWrapper(stream).getResult();
      } else if (jsonNodes.has("entry")) {
        result = (Group20Entry) parse(stream, Group20Entry.class);
      } else {
        throw new RuntimeException("Unrecognized JSON " + json);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  private ResultWrapper<Group20Entry> parseGroup20ResultWrapper(InputStream in) {
    try {
      return objectMapper.readValue(in, new TypeReference<ResultWrapper<Group20Entry>>() {
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private ResultWrapper<GroupMembersEntry> parseTeamMembersResultWrapper(InputStream in) {
    try {
      return objectMapper.readValue(in, new TypeReference<ResultWrapper<GroupMembersEntry>>() {
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Object parse(InputStream in, Class<? extends AbstractEntry> entry) {
    try {
      return objectMapper.readValue(in, entry);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @return the objectMapper
   */
  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  /**
   * @param objectMapper the objectMapper to set
   */
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

}
