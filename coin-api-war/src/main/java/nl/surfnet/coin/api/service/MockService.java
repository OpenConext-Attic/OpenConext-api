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

package nl.surfnet.coin.api.service;

import org.json.JSONArray;

import nl.surfnet.coin.api.client.OpenConextJsonParser;
import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.GroupEntry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Name;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.domain.PersonEntry;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class MockService implements PersonService, GroupService {

  private Logger LOG = LoggerFactory.getLogger(MockService.class);

  private final static String JSON_PATH = "json/%s-%s.json";
  private final static String FALLBACK = "fallback";
  private OpenConextJsonParser parser = new OpenConextJsonParser();

  @Override
  public PersonEntry getPerson(String userId, String loggedInUser) {

    /*
      Strip all characters that might cause problems in filenames.
      e.g.:
      urn:collab:person:test.surfguest.nl:foo becomes:
      urn_collab_person_test.surfguest.nl_foo
     */
    String userIdStripped = userId.replaceAll("[^0-9a-zA-Z_.-]", "_");

    final String filename = String.format(JSON_PATH, userIdStripped, "person");
    LOG.debug("filename: {}", filename);
    ClassPathResource pathResource = new ClassPathResource(filename);
    if (!pathResource.exists()) {
      pathResource = new ClassPathResource(String.format(JSON_PATH, FALLBACK, "person"));
    }
    try {
      return parser.parsePerson(pathResource.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.service.PersonService#getGroupMembers(java.lang.String,
   * java.lang.String)
   */
  @Override
  public GroupMembersEntry getGroupMembers(String groupId, String onBehalfOf) {
    ClassPathResource pathResource = new ClassPathResource(String.format(
        JSON_PATH, groupId, "teammembers"));
    if (!pathResource.exists()) {
      pathResource = new ClassPathResource(String.format(JSON_PATH, FALLBACK,
          "teammembers"));
    }
    try {
      return parser.parseTeamMembers(pathResource.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.api.service.GroupService#getGroups(java.lang.String,
   * java.lang.String)
   */
  @Override
  public GroupEntry getGroups(String userId, String onBehalfOf) {
    ClassPathResource pathResource = new ClassPathResource(String.format(
        JSON_PATH, userId, "groups"));
    if (!pathResource.exists()) {
      pathResource = new ClassPathResource(String.format(JSON_PATH, FALLBACK,
          "groups"));
    }
    try {
      return parser.parseGroups(pathResource.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /* (non-Javadoc)
   * @see nl.surfnet.coin.api.service.GroupService#getGroups20(java.lang.String, java.lang.String)
   */
  @Override
  public Group20Entry getGroups20(String userId, String onBehalfOf) {
    ClassPathResource pathResource = new ClassPathResource(String.format(
        JSON_PATH, userId, "groups20"));
    if (!pathResource.exists()) {
      pathResource = new ClassPathResource(String.format(JSON_PATH, FALLBACK,
          "groups20"));
    }
    try {
      return parser.parseGroups20(pathResource.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
