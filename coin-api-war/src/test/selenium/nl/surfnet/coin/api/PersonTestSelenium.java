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
package nl.surfnet.coin.api;

import nl.surfnet.coin.api.client.OpenConextApi20ThreeLegged;
import nl.surfnet.coin.mock.MockHandler;
import nl.surfnet.coin.mock.MockHtppServer;

import org.eclipse.jetty.server.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.*;
import org.scribe.model.Response;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.System;
import java.lang.Thread;

import static org.junit.Assert.assertTrue;

/**
 * Test Person related queries with selenium
 */
public class PersonTestSelenium extends SeleniumSupport {

  private Logger LOG = LoggerFactory.getLogger(PersonTestSelenium.class);

  private final String OAUTH_KEY = "https://testsp.test.surfconext.nl/test";
  private final String OAUTH_SECRET = "mysecret";

  private final String OAUTH_CALLBACK_URL = "http://localhost:8083/";

  private final String SURFCONEXT_BASE_URL = "http://localhost:8095/";

  private final String USER_ID = "urn:collab:person:test.surfguest.nl:oharsta";
  private MockHtppServer server;


  @Test
  public void completeFlow() throws Exception {

    OAuthService service = new ServiceBuilder()
        .provider(OpenConextApi20ThreeLegged.class)
        .apiKey(OAUTH_KEY)
        .apiSecret(OAUTH_SECRET)
        .callback(OAUTH_CALLBACK_URL)
        .build();
    String authUrl = service.getAuthorizationUrl(null);
    LOG.debug("Auth url: {}", authUrl);


    getWebDriver().get(authUrl);
    // log in...
    getWebDriver().findElementByName("j_username").sendKeys("bob");
    getWebDriver().findElementByName("j_password").sendKeys("bobspassword");
    getWebDriver().findElementByName("submit").click();

    // Wait for authorizationCode to be sent to the mock http server
    while (authorizationCode == null) {
      Thread.sleep(100L);
    }

    LOG.debug("authorizationCode is not null anymore: " + authorizationCode);
    Token aToken = service.getAccessToken(null, authorizationCode);

    OAuthRequest request = new OAuthRequest(Verb.GET, SURFCONEXT_BASE_URL + "rest/people/" + USER_ID + "/@self");

    service.signRequest(aToken, request);
    Response response = request.send();
    assertTrue(response.getBody().contains("mock-name"));
    LOG.debug("Response: {}", response.getBody());
  }

  private Verifier authorizationCode;

  @After
  public void stopServer() {
    LOG.debug("Stopping server...");
    server.stopServer();
  }

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
            LOG.debug("Request to mock http server: {}", request);
            authorizationCode = new Verifier(request.getParameter("code"));
            response.setStatus(200);
          }
        };
      }
    };
    server.startServer();
  }
}
