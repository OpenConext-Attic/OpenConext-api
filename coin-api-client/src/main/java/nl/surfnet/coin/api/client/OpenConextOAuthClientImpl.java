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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.GroupEntry;
import nl.surfnet.coin.api.client.domain.GroupMembersEntry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.domain.PersonEntry;

import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.util.StringUtils;

/**
 * Implementation of OpenConextOAuthClient
 * 
 */
public class OpenConextOAuthClientImpl implements OpenConextOAuthClient {

  private static final String REQUEST_TOKEN = "REQUEST_TOKEN";
  private ObjectMapper objectMapper = new ObjectMapper();
  private OAuthEnvironment environment;
  private OAuthRepository repository;

  public OpenConextOAuthClientImpl(OAuthEnvironment environment,
      OAuthRepository repository) {
    super();
    this.environment = environment;
    this.repository = repository;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#isAccessTokenGranted(java
   * .lang.String)
   */
  @Override
  public boolean isAccessTokenGranted(String userId) {
    return repository.getToken(userId) != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#redirectToAuthorizationUrl
   * (javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void redirectToAuthorizationUrl(OAuthVersion version,
      HttpServletRequest request, HttpServletResponse response) {
    OAuthService service = getService(version, OAuthProtocol.threelegged);
    Token requestToken = service.getRequestToken();
    String authUrl = service.getAuthorizationUrl(requestToken);
    request.getSession().setAttribute(REQUEST_TOKEN, requestToken);
    try {
      response.sendRedirect(authUrl);
    } catch (IOException e) {
      throw new RuntimeException("IOexception occured when redirecting to :"
          + authUrl, e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#oauthCallback(javax.servlet
   * .http.HttpServletRequest)
   */
  @Override
  public void oauthCallback(OAuthVersion version, HttpServletRequest request) {
    String oAuthVerifier = request.getParameter("oauth_verifier");
    Verifier verifier = new Verifier(oAuthVerifier);
    String userId = request.getParameter("user_id");
    try {
      userId = URLDecoder.decode(userId, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    OAuthService service = getService(version, OAuthProtocol.threelegged);
    Token requestToken = (Token) request.getSession().getAttribute(
        REQUEST_TOKEN);
    Token accessToken = service.getAccessToken(requestToken, verifier);
    repository.storeToken(accessToken, userId, version);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#getPerson(java.lang.String
   * , java.lang.String)
   */
  @Override
  public Person getPerson(String userId, String onBehalfOf) {
    OAuthRequest request = new OAuthRequest(Verb.GET,
        environment.getEndpointBaseUrl() + "social/rest/people/" + userId
            + "/@self");
    PersonEntry entry = (PersonEntry) execute(onBehalfOf, request, PersonEntry.class);
    return entry.getEntry();
  }


  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#getPeople(java.lang.String
   * , java.lang.String)
   */
  @Override
  public List<Person> getGroupMembers(String groupId, String onBehalfOf) {
    if (!StringUtils.hasText(onBehalfOf)) {
      throw new IllegalArgumentException("For retrieving group members the onBehalfOf may not be empty");
    }
    OAuthRequest request = new OAuthRequest(Verb.GET,
        environment.getEndpointBaseUrl() + "social/rest/people/" + onBehalfOf
            + "/" + groupId);
    GroupMembersEntry entry = (GroupMembersEntry) execute(onBehalfOf, request, GroupMembersEntry.class);
    return entry.getEntry();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#getGroups(java.lang.String
   * , java.lang.String)
   */
  @Override
  public List<Group> getGroups(String userId, String onBehalfOf) {
    OAuthRequest request = new OAuthRequest(Verb.GET,
        environment.getEndpointBaseUrl() + "social/rest/groups/" + userId);
    GroupEntry entry = (GroupEntry) execute(onBehalfOf, request, GroupEntry.class);
    return entry.getEntry();
  }
  
  private Object execute(String onBehalfOf, OAuthRequest request, Class<? extends Serializable> parseType) {
    Token token;
    OAuthService service;
    if (onBehalfOf == null) {
      // two legged and therefore v10a
      service = getService(OAuthVersion.v10a, OAuthProtocol.twolegged);
      token = new Token("", "");
    } else {
      OAuthToken oAuthToken = repository.getToken(onBehalfOf);
      if (oAuthToken == null) {
        throw new RuntimeException("No access token present for user('"
            + onBehalfOf + "'). First obtain an accesstoken.");
      }
      token = oAuthToken.getToken();
      service = getService(oAuthToken.getVersion(), OAuthProtocol.threelegged);
    }
    service.signRequest(token, request);
    Response oAuthResponse = request.send();
    InputStream stream = oAuthResponse.getStream();
    Object entry;
    try {
      entry = objectMapper.readValue(stream, parseType);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return entry;
  }

  
  private OAuthService getService(OAuthVersion version, OAuthProtocol protocol) {
    String baseUrl = environment.getEndpointBaseUrl();
    Api api = version.equals(OAuthVersion.v10a) ? (protocol
        .equals(OAuthProtocol.twolegged) ? new OpenConextApi10aTwoLegged()
        : new OpenConextApi10aThreeLegged(baseUrl))
        : new OpenConextApi20ThreeLeggedDeletme(baseUrl);
    OAuthService service = new ServiceBuilder().provider(api)
        .apiKey(environment.getOauthKey())
        .apiSecret(environment.getOauthSecret())
        .callback(environment.getCallbackUrl()).build();
    return service;
  }

}
