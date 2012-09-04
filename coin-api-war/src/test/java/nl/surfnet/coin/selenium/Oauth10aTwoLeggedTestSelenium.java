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

import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.surfnet.coin.api.client.internal.OpenConextApi10aTwoLegged;

import static org.junit.Assert.assertTrue;

public class Oauth10aTwoLeggedTestSelenium extends SeleniumSupport {

  private Logger LOG = LoggerFactory.getLogger(Oauth10aTwoLeggedTestSelenium.class);

  private static final String OAUTH_KEY = "https://testsp.test.surfconext.nl/shibboleth";
  private static final String OAUTH_SECRET = "mysecret";

  private static final String USER_ID = "mock-shib-remote-user";
  private static final String OS_URL = "social/rest/people/" + USER_ID;

  @Test
  public void testTwoLegged() {
    OAuthService service = new ServiceBuilder()
        .provider(new OpenConextApi10aTwoLegged())
        .apiKey(OAUTH_KEY)
        .apiSecret(OAUTH_SECRET)
        .debug()
        .build();
    Token token = new Token("", "");

    OAuthRequest req = new OAuthRequest(Verb.GET, getApiBaseUrl() + OS_URL);
    service.signRequest(token, req);
    LOG.debug("Signed resource request: {}", req.toString());

    Response response = req.send();
    String bodyText = response.getBody();
    LOG.debug("Response body: {}", bodyText);
    assertTrue("response body should contain correct json data", bodyText.contains("mock-shib-remote-user"));
    
    //also test the mock 
    
    req = new OAuthRequest(Verb.GET, (getApiBaseUrl() + OS_URL).replace("/social/rest/", "/mock10/social/rest/"));
    service.signRequest(token, req);
    LOG.debug("Signed resource request: {}", req.toString());

    response = req.send();
    bodyText = response.getBody();
    LOG.debug("Response body: {}", bodyText);
    assertTrue(bodyText.contains("mnice@surfguest.nl"));

  }
}