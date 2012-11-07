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

package nl.surfnet.coin.selenium;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.surfnet.coin.api.client.internal.OpenConextApi20Implicit;
import nl.surfnet.coin.mock.MockHandler;
import nl.surfnet.coin.mock.MockHtppServer;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * Test Person related queries with selenium
 */
public class Oauth20ImplicitGrantTestSelenium extends SeleniumSupport {

  private static final String OAUTH_CALLBACK_URL = "http://localhost:8083/";
  private Logger LOG = LoggerFactory
    .getLogger(Oauth20ImplicitGrantTestSelenium.class);

  private static final String OAUTH_KEY = "https://testsp.test.surfconext.nl/shibboleth";
  private static final String OAUTH_SECRET = "mysecret";
  private final static String OAUTH_OPENCONEXT_API_READ_SCOPE = "read";

  private MockHtppServer server;

  private String callbackRequestFragment;

  @Before
  public void startServer() {
    LOG.debug("Starting server for catching authorization code...");
    server = new MockHtppServer(8083) {
      protected MockHandler createHandler(Server server) {
        return new MockHandler(server) {
          @Override
          public void handle(String target, Request baseRequest,
                             HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
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

  @Before
  public void clearCookies() {
    getRestartedWebDriver();
  }


  @After
  public void stopServer() {
    LOG.debug("Stopping server...");
    server.stopServer();
  }

  @Before
  public void letMujinaReturnUrnCollabPerson() {
    letMujinaSendUrnCollabAttribute("some-user");
  }

  @Test
  public void implicitGrant() throws Exception {

    final String restUrl = getApiBaseUrl() + "social/rest/people/foo/@self";
    getWebDriver().get(restUrl);
    LOG.debug("Page source before authentication: "
      + getWebDriver().getPageSource());
    assertFalse(
      "Result of getPerson-call should fail because of missing authentication",
      getWebDriver().getPageSource().contains("Mister Foo"));

    OAuthService service = new ServiceBuilder()
      .provider(OpenConextApi20Implicit.class).apiKey(OAUTH_KEY)
      .apiSecret(OAUTH_SECRET).callback(OAUTH_CALLBACK_URL)
      .scope(OAUTH_OPENCONEXT_API_READ_SCOPE).build();
    String authUrl = service.getAuthorizationUrl(null);
    LOG.debug("Auth url: {}", authUrl);
    getWebDriver().get(authUrl);
    loginAtMujinaIfNeeded("user");

    // Authorize on user consent page
    giveUserConsentIfNeeded();

    URI uri = URI.create(getWebDriver().getCurrentUrl());
    LOG.debug("URL is: " + uri.toString());
    LOG.debug("Response body is: " + getWebDriver().getPageSource());
    callbackRequestFragment = uri.getFragment();
    assertNotNull("redirect URL should contain fragment.",
      callbackRequestFragment);
    assertTrue("redirect URL fragment should contain access token",
      callbackRequestFragment.contains("access_token="));
    assertFalse("redirect URL fragment should NOT contain an expires_in parameter", callbackRequestFragment.contains("expires_in="));
    // Further tests are actually part of the coin-api-client... The server has issued an access_token so it works.
  }

  @Test
  public void implicitGrantWithDeny() throws Exception {
    OAuthService service = new ServiceBuilder()
      .provider(OpenConextApi20Implicit.class).apiKey(OAUTH_KEY.concat(UUID.randomUUID().toString()))
      .apiSecret(OAUTH_SECRET.concat("force_consent"))
      .callback(OAUTH_CALLBACK_URL).scope(OAUTH_OPENCONEXT_API_READ_SCOPE)
      .build();
    String authUrl = service.getAuthorizationUrl(null);
    LOG.debug("Auth url: {}", authUrl);
    getWebDriver().get(authUrl);
    loginAtMujinaIfNeeded("user");

    // Deny on user consent page
    WebElement authorizeButton = getWebDriver().findElement(
      By.id("decline_terms_button"));
    authorizeButton.click();

    URI uri = URI.create(getWebDriver().getCurrentUrl());
    LOG.debug("URL is: " + uri.toString());
    LOG.debug("Response body is: " + getWebDriver().getPageSource());
    callbackRequestFragment = uri.getFragment();
    assertNotNull("redirect URL should contain fragment.",
      callbackRequestFragment);
    assertFalse("redirect URL fragment should not contain access token",
      callbackRequestFragment.contains("access_token="));
    assertFalse("redirect URL fragment should contain access token",
      callbackRequestFragment.contains("access_token="));
  }


  @Test
  public void noRedirectUriGiven() {
    String url = getApiBaseUrl() + "oauth2/authorize?client_id=someclient&response_type=token";

    getWebDriver().get(url);

    loginAtMujinaIfNeeded("user");

    // Authorize on user consent page
    giveUserConsentIfNeeded();

    String pageSource = getWebDriver().getPageSource();
    LOG.debug("Response body is: " + pageSource);
    assertTrue("Page should contain correct error message", pageSource.contains("does not match one of the registered values"));
    assertFalse("Page should not be a 500", pageSource.contains("500"));
//    callbackRequestFragment = uri.getFragment();

  }
}
