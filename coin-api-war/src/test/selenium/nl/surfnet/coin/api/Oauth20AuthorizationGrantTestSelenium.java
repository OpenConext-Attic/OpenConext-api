/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.coin.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.surfnet.coin.api.client.OpenConextApi20AuthorizationCode;
import nl.surfnet.coin.mock.MockHandler;
import nl.surfnet.coin.mock.MockHtppServer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test Person related queries with selenium
 */
public class Oauth20AuthorizationGrantTestSelenium extends SeleniumSupport {

  private Logger LOG = LoggerFactory.getLogger(Oauth20AuthorizationGrantTestSelenium.class);

  private static final String OAUTH_KEY = "https://testsp.dev.surfconext.nl/shibboleth";
  private static final String OAUTH_SECRET = "mysecret";

  private static final String OAUTH_CALLBACK_URL = "http://localhost:8083/";

  private static final String USER_ID = "mock-shib-remote-user";
  private static final String GROUP_ID = "mock-shib-remote-group";

  private final static String OAUTH_OPENCONEXT_API_READ_SCOPE = "read";

  private MockHtppServer server;

  private Verifier authorizationCode;

  @Before
  public void clearCookies() {
    getWebDriver().manage().deleteAllCookies();
  }

  @Before
  public void startServer() {
    LOG.debug("Starting server for catching authorization code...");
    server = new MockHtppServer(8083) {
      protected MockHandler createHandler(Server server) {
        return new MockHandler(server) {
          @Override
          public void handle(String target, Request baseRequest, HttpServletRequest request,
              HttpServletResponse response) throws IOException, ServletException {
            if (request.getRequestURI().contains("favicon")) {
              LOG.debug("ignoring favicon-request.");
              return;
            }
            LOG.debug("Request to mock http server: {}", request);
            authorizationCode = new Verifier(request.getParameter("code"));
            response.setStatus(200);
          }
        };
      }
    };
    server.startServer();
  }

  @After
  public void stopServer() {
    LOG.debug("Stopping server...");
    server.stopServer();
  }

  @Test
  public void authorizationCodeGrant() throws Exception {
    OAuthService service = new ServiceBuilder().provider(new OpenConextApi20AuthorizationCode(getApiBaseUrl()))
        .apiKey(OAUTH_KEY).apiSecret(OAUTH_SECRET).scope(OAUTH_OPENCONEXT_API_READ_SCOPE).callback(OAUTH_CALLBACK_URL)
        .build();
    String authUrl = service.getAuthorizationUrl(null);
    LOG.debug("Auth url: {}", authUrl);

    getWebDriver().get(authUrl);

    // Authorize on user consent page
    giveUserConsentIfNeeded();

    // Wait for authorizationCode to be sent to the mock http server
    while (authorizationCode == null) {
      Thread.sleep(100L);
    }

    LOG.debug("authorizationCode is not null anymore: " + authorizationCode);
    Token aToken = service.getAccessToken(null, authorizationCode);

    String restUrl = getApiBaseUrl() + "social/rest/people/" + USER_ID;

    // Verify that a normal request (without access token) fails now.
    getWebDriver().manage().deleteAllCookies();
    getWebDriver().get(restUrl);
    assertFalse(getWebDriver().getPageSource().contains("mnice@surfguest.nl"));

    getWebDriver().manage().deleteAllCookies();
    OAuthRequest request = new OAuthRequest(Verb.GET, restUrl);
    service.signRequest(aToken, request);
    Response response = request.send();
    String body = response.getBody();
    LOG.debug("Response: {}", body);
    assertTrue(body.contains("foo@example.com"));

    restUrl = getApiBaseUrl() + "social/rest/people/" + USER_ID + "/" + GROUP_ID;
    request = new OAuthRequest(Verb.GET, restUrl);
    service.signRequest(aToken, request);
    response = request.send();
    LOG.debug("Response: {}", body);
    assertTrue(body.contains("foo@example.com"));

    // also test the mock
    restUrl = getApiBaseUrl() + "mock10/social/rest/people/" + USER_ID;
    request = new OAuthRequest(Verb.GET, restUrl);
    service.signRequest(aToken, request);
    response = request.send();
    LOG.debug("Response: {}", body);
    assertTrue(body.contains("foo@example.com"));

  }

}
