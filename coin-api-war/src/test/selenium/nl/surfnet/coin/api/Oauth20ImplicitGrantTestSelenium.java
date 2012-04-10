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

package nl.surfnet.coin.api;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.surfnet.coin.api.client.OpenConextApi20Implicit;
import nl.surfnet.coin.mock.MockHandler;
import nl.surfnet.coin.mock.MockHtppServer;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test Person related queries with selenium
 */
public class Oauth20ImplicitGrantTestSelenium extends SeleniumSupport {

  private Logger LOG = LoggerFactory.getLogger(Oauth20ImplicitGrantTestSelenium.class);

  private static final String OAUTH_KEY = "https://testsp.test.surfconext.nl/shibboleth";
  private static final String OAUTH_SECRET = "mysecret";
  private final static String OAUTH_OPENCONEXT_API_READ_SCOPE = "read";
  private static final String OAUTH_CALLBACK_URL = "http://localhost:8083/";

  private MockHtppServer server;

  private String callbackRequestFragment;


  @Before
  public void startServer() {
    LOG.debug("Starting server for catching authorization code...");
    server = new MockHtppServer(8083) {
      protected MockHandler createHandler(Server server) {
        return new MockHandler(server) {
          @Override
          public void handle(String target, Request baseRequest, HttpServletRequest request,
                             HttpServletResponse response) throws IOException,
              ServletException {
            if (request.getRequestURI().contains("favicon")) {
              LOG.debug("ignoring favicon-request.");
              return;
            }
            LOG.debug("Request to mock http server: {}", request);
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
  public void implicitGrant() throws Exception {

    final String restUrl = getApiBaseUrl() + "social/rest/people/foo/@self";
    getWebDriver().get(restUrl);
    LOG.debug("Page source before authentication: " + getWebDriver().getPageSource());
    assertFalse("Result of getPerson-call should fail because of missing authentication", getWebDriver().getPageSource().contains("Mister Foo"));


    OAuthService service = new ServiceBuilder()
        .provider(OpenConextApi20Implicit.class)
        .apiKey(OAUTH_KEY)
        .apiSecret(OAUTH_SECRET)
        .callback(restUrl)
        .scope(OAUTH_OPENCONEXT_API_READ_SCOPE)
        .build();
    String authUrl = service.getAuthorizationUrl(null);
    LOG.debug("Auth url: {}", authUrl);

    getWebDriver().get(authUrl);

//    loginEndUser();

    // Authorize on user consent page
    giveUserConsentIfNeeded();

    URI uri = URI.create(getWebDriver().getCurrentUrl());
    callbackRequestFragment = uri.getFragment();
    LOG.debug("URL is: " + uri.toString());
    assertTrue("redirect URL fragment should contain access token", callbackRequestFragment.contains("access_token="));

    // Further tests are actually part of the coin-api-client... The server has issued an access_token so it works.
  }



}
