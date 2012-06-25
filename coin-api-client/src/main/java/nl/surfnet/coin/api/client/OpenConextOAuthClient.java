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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;

/**
 * Capable of retrieving OpenSocial data from OpenSocial endpoints
 * 
 */
public interface OpenConextOAuthClient {

  /**
   * Is it possible to make OpenSocial requests for this user (as onBehalfOf).
   * In principle clients need to make this check every time they want to make a
   * three-legged oauth call.
   * 
   * @param userId
   *          the unique identifier
   * @return true if there is a valid access token for the user
   */
  boolean isAccessTokenGranted(String userId);

  /**
   * Get the authorization url
   * 
   * @return the authorization url
   */
  String getAuthorizationUrl();

  /**
   * Clients who have requested a redirect to the authorization URL will be
   * notified on the registered callback URL. For extracting the oauth access
   * token they will need to 'forward' the call back request to this method.
   *
   * @param request    the HTTP request for obtaining the request token
   * @param onBehalfOf the userId of the end user
   */
  void oauthCallback(HttpServletRequest request, String onBehalfOf);

  /**
   * Get the OpenSocial Person. Note that the onBehalfOf is only necessary if
   * this is a three-legged call (e.g. there is an access token for the
   * onBehalfOf). For two legged calls the onBehalfOf can be null.
   * 
   * @param userId
   *          the unique identifier
   * @param onBehalfOf
   *          the unique identifier of the user that is going to make the
   *          request
   * @return the {@link Person}
   */
  Person getPerson(String userId, String onBehalfOf);

  /**
   * Get the group members of the given group. Note that the onBehalfOf is only
   * necessary if this is a three-legged call (e.g. there is an access token for
   * the onBehalfOf). For two legged calls the onBehalfOf can be null.
   * 
   * @param groupId
   *          {@link String} the unique identifier for the group
   * @param onBehalfOf
   *          {@link String} the unique identifier of the user that is going to
   *          make the request
   * @return an {@link java.util.ArrayList} containing {@link Person}'s
   */
  List<Person> getGroupMembers(String groupId, String onBehalfOf);

  /**
   * Get Persons' Groups
   * 
   * @param userId
   *          the unique identifier
   * @param onBehalfOf
   *          the unique identifier of the user that is going to make the
   *          request
   * @return {@link List} containing the {@link Group}s
   */
  List<Group> getGroups(String userId, String onBehalfOf);

  /**
   * Get Persons' Groups
   * 
   * @param userId
   *          the unique identifier
   * @param onBehalfOf
   *          the unique identifier of the user that is going to make the
   *          request
   * @return {@link List} containing the {@link Group}s
   */
  List<Group20> getGroups20(String userId, String onBehalfOf);

}
