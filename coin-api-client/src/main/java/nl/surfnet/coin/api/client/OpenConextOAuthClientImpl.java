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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;

/**
 * Implementation of OpenConextOAuthClient
 */
public class OpenConextOAuthClientImpl implements OpenConextOAuthClient, InitializingBean {

  private static final Logger LOG = LoggerFactory.getLogger(OpenConextOAuthClientImpl.class);

  private static final String REQUEST_TOKEN = "REQUEST_TOKEN";
  private OAuthEnvironment environment;
  private OAuthRepository repository;
  private OpenConextJsonParser parser = new OpenConextJsonParser();

  public OpenConextOAuthClientImpl() {
    this.environment = new OAuthEnvironment();

    environment.setVersion(OAuthVersion.v2);

    this.repository = new InMemoryOAuthRepositoryImpl();
  }

  @Override
  public boolean isAccessTokenGranted(String userId) {
    return repository.getToken(userId) != null;
  }

  private String doGetAuthorizationUrl(HttpServletRequest request) {
    final OAuthVersion version = environment.getVersion();
    OAuthService service = getService(version, OAuthProtocol.threelegged);
    Token requestToken;
    if (OAuthVersion.v10a.equals(version)) {
      requestToken = service.getRequestToken();
      if (request != null) {
        request.getSession().setAttribute(REQUEST_TOKEN, requestToken);
      }
    } else {
      requestToken = null;
    }
    return service.getAuthorizationUrl(requestToken);
  }

  @Override
  public String getAuthorizationUrl() {
    return doGetAuthorizationUrl(null);
  }

  @Override
  public void oauthCallback(HttpServletRequest request, String onBehalfOf) {
    String oAuthVerifier;
    Token requestToken = null;
    OAuthVersion version = environment.getVersion();
    if (OAuthVersion.v10a.equals(version)) {
      oAuthVerifier = request.getParameter("oauth_verifier");
      requestToken = (Token) request.getSession().getAttribute(REQUEST_TOKEN);
    } else {
      oAuthVerifier = request.getParameter("code");
    }
    Verifier verifier = new Verifier(oAuthVerifier);

    OAuthService service = getService(version, OAuthProtocol.threelegged);
    Token accessToken = service.getAccessToken(requestToken, verifier);
    repository.storeToken(accessToken, onBehalfOf, version);
  }


  @Override
  public Person getPerson(String userId, String onBehalfOf) {
    OAuthRequest request = new OAuthRequest(Verb.GET,
        environment.getEndpointBaseUrl() + "social/rest/people/" + userId);
    InputStream in = execute(onBehalfOf, request);
    return parser.parsePerson(in).getEntry();
  }

  @Override
  public List<Person> getGroupMembers(String groupId, String onBehalfOf) {
    if (!StringUtils.hasText(onBehalfOf)) {
      throw new IllegalArgumentException(
          "For retrieving group members the onBehalfOf may not be empty");
    }
    OAuthRequest request = new OAuthRequest(Verb.GET,
        environment.getEndpointBaseUrl() + "social/rest/people/" + onBehalfOf
            + "/" + groupId);
    InputStream in = execute(onBehalfOf, request);
    return parser.parseTeamMembers(in).getEntry();
  }

  @Override
  public List<Group> getGroups(String userId, String onBehalfOf) {
    OAuthRequest request = new OAuthRequest(Verb.GET,
        environment.getEndpointBaseUrl() + "social/rest/groups/" + userId);
    InputStream in = execute(onBehalfOf, request);
    return parser.parseGroups(in).getEntry();
  }

  @Override
  public List<Group20> getGroups20(String userId, String onBehalfOf) {
    OAuthRequest request = new OAuthRequest(Verb.GET,
        environment.getEndpointBaseUrl() + "social/rest/groups/" + userId);
    InputStream in = execute(onBehalfOf, request);
    return parser.parseGroups20(in).getEntry();
  }

  private InputStream execute(String onBehalfOf, OAuthRequest request) {
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
    if (LOG.isDebugEnabled()) {
      LOG.debug("Will send request '{}'", request.toString());
    }
    Response oAuthResponse = request.send();
    if (oAuthResponse.getCode() >= 400) {
      throw new RuntimeException(String.format("Error response: %d, body: %s", oAuthResponse.getCode(),
          oAuthResponse.getStream() == null ? null : oAuthResponse.getBody()));
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

  private OAuthService getService(OAuthVersion version, OAuthProtocol protocol) {
    String baseUrl = environment.getEndpointBaseUrl();
    Api api;

    if (version.equals(OAuthVersion.v10a)) {
      api = protocol.equals(OAuthProtocol.twolegged) ? new OpenConextApi10aTwoLegged() : new OpenConextApi10aThreeLegged(baseUrl);
    } else {
      api = new OpenConextApi20AuthorizationCode(baseUrl);
    }


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
  public void setVersion(OAuthVersion v) {
    environment.setVersion(v);
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
}
