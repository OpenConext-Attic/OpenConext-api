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

import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.internal.OAuth2Grant;
import nl.surfnet.coin.api.client.internal.OpenConextApi20AuthorizationCode;
import nl.surfnet.coin.api.client.internal.OpenConextApi20ClientCredentials;
import org.apache.commons.io.IOUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Implementation of OpenConextOAuthClient
 */
public class OpenConextOAuthClientImpl implements OpenConextOAuthClient, InitializingBean {

  private static final Logger LOG = LoggerFactory.getLogger(OpenConextOAuthClientImpl.class);
  private static final int MAX_ACCESS_TOKEN_REQUESTS = 3;

  private OAuthEnvironment environment;
  private OAuthRepository repository;
  private OpenConextJsonParser parser = new OpenConextJsonParser();

  public OpenConextOAuthClientImpl() {
    this.environment = new OAuthEnvironment();
    this.repository = new InMemoryOAuthRepositoryImpl();
  }

  @Override
  public boolean isAccessTokenGranted(String userId) {
    return repository.getToken(userId) != null;
  }

  private String doGetAuthorizationUrl(HttpServletRequest request) {
    OAuthService service = getService(OAuth2Grant.authorizationCode);
    return service.getAuthorizationUrl(null);
  }

  @Override
  public String getAuthorizationUrl() {
    return doGetAuthorizationUrl(null);
  }

  @Override
  public void oauthCallback(HttpServletRequest request, String onBehalfOf) {
    String oAuthVerifier;
    Token requestToken = null;
    oAuthVerifier = request.getParameter("code");
    Verifier verifier = new Verifier(oAuthVerifier);

    OAuthService service = getService(OAuth2Grant.authorizationCode);
    String accessToken = service.getAccessToken(requestToken, verifier).getToken();
    repository.storeToken(accessToken, onBehalfOf);
  }


  @Override
  public Person getPerson(String userId, String onBehalfOf) {
    OAuthRequest request = new OAuthRequest(Verb.GET, environment.getEndpointBaseUrl() + "social/rest/people/" + userId);
    InputStream in = execute(onBehalfOf, request);
    return parser.parsePerson(in).getEntry();
  }

  @Override
  public List<Person> getGroupMembers(String groupId, String onBehalfOf) {
    if (!StringUtils.hasText(onBehalfOf)) {
      throw new IllegalArgumentException(
          "For retrieving group members the onBehalfOf may not be empty");
    }
    OAuthRequest request = new OAuthRequest(Verb.GET, environment.getEndpointBaseUrl() + "social/rest/people/" + onBehalfOf + "/" + groupId);
    InputStream in = execute(onBehalfOf, request);
    return parser.parseTeamMembers(in).getEntry();
  }

  @Override
  public List<Group> getGroups(String userId, String onBehalfOf) {
    OAuthRequest request = new OAuthRequest(Verb.GET, environment.getEndpointBaseUrl() + "social/rest/groups/" + userId);
    InputStream in = execute(onBehalfOf, request);
    return parser.parseGroups(in).getEntry();
  }

  @Override
  public List<Group20> getGroups20(String userId, String onBehalfOf) {
    OAuthRequest request = new OAuthRequest(Verb.GET, environment.getEndpointBaseUrl() + "social/rest/groups/" + userId);
    InputStream in = execute(onBehalfOf, request);
    return parser.parseGroups20(in).getEntry();
  }

  @Override
  public Group20 getGroup20(String userId, String groupId, String onBehalfOf) {
    final String url = String.format("%ssocial/rest/groups/%s/%s", environment.getEndpointBaseUrl(), userId, groupId);
    OAuthRequest request = new OAuthRequest(Verb.GET, url);
    InputStream in = execute(onBehalfOf, request);
    final List<Group20> entry = parser.parseGroups20(in).getEntry();
    if (entry != null && entry.size() > 0) {
      return entry.get(0);
    }
    return null;
  }

  private InputStream execute(String onBehalfOf, OAuthRequest request) {
    String token;
    OAuthService service;

    token = repository.getToken(onBehalfOf);
    if (onBehalfOf == null) {
      int retries = 0;
      while (token == null && retries < MAX_ACCESS_TOKEN_REQUESTS) {
        getClientAccessToken();
        token = repository.getToken(onBehalfOf);
      }
      service = getService(OAuth2Grant.clientCredentials);
    } else {
      if (token == null) {
        throw new RuntimeException("No access token present for user('" + onBehalfOf + "'). First obtain an accesstoken.");
      }
      service = getService(OAuth2Grant.authorizationCode);
    }

    service.signRequest(new Token(token, ""), request);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Will send request '{}'", request.toString());
    }
    Response oAuthResponse = request.send();

    if (oAuthResponse.getCode() >= 400) {
      if (oAuthResponse.getCode() == 401
              && oAuthResponse.getStream() != null
              && oAuthResponse.getBody().contains("invalid_token")) {
        repository.removeToken(onBehalfOf);
        throw new InvalidTokenException(oAuthResponse.getBody());
      } else {
        // This could be refined to include other cases, and throw according exceptions.
        throw new RuntimeException(String.format("Error response: %d, body: %s", oAuthResponse.getCode(), oAuthResponse.getStream() == null ? null : oAuthResponse.getBody()));
      }
    }

    InputStream stream = oAuthResponse.getStream();
    if (LOG.isDebugEnabled()) {
      stream = logInputStream(stream);
    }
    return stream;
  }

  private InputStream logInputStream(InputStream stream) {
    String json;
    try {
      json = IOUtils.toString(stream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    LOG.debug(json);
    stream = IOUtils.toInputStream(json);
    return stream;
  }

  private OAuthService getService(OAuth2Grant grantType) {
    String baseUrl = environment.getEndpointBaseUrl();
    Api api;

      api = grantType.equals(OAuth2Grant.clientCredentials) ? new OpenConextApi20ClientCredentials(baseUrl) : new OpenConextApi20AuthorizationCode(baseUrl);

    return new ServiceBuilder()
        .provider(api)
        .apiKey(environment.getOauthKey())
        .scope("read")
        .apiSecret(environment.getOauthSecret())
        .callback(environment.getCallbackUrl()).build();
  }

  public void setCallbackUrl(String url) {
    environment.setCallbackUrl(url);
  }
  public void setConsumerSecret(String secret) {
    environment.setOauthSecret(secret);
  }
  public void setConsumerKey(String key) {
    environment.setOauthKey(key);
  }
  public void setEndpointBaseUrl(String url) {
    environment.setEndpointBaseUrl(url);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(environment);
    Assert.notNull(environment.getEndpointBaseUrl(), "endpoint base url cannot be null");
    Assert.notNull(repository);
  }

  public void setRepository(OAuthRepository repository) {
    this.repository = repository;
  }

  public void getClientAccessToken() {

    Token accessToken = getService(OAuth2Grant.clientCredentials).getAccessToken(new Token("", ""), new Verifier(""));
    LOG.debug("Received access token from OAuth 2 server: " + accessToken);
    repository.storeToken(accessToken.getToken(), null);
  }
}
