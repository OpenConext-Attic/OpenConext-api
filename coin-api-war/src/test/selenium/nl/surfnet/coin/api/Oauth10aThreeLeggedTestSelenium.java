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

import org.junit.Test;
import org.openqa.selenium.By;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.surfnet.coin.api.client.OpenConextApi10aThreeLegged;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Oauth10aThreeLeggedTestSelenium extends SeleniumSupport {

  private Logger LOG = LoggerFactory.getLogger(Oauth10aThreeLeggedTestSelenium.class);

  private static final String OAUTH_KEY = "https://testsp.test.surfconext.nl/shibboleth";
  private static final String OAUTH_SECRET = "mysecret";

  private static final String USER_ID = "mock-shib-remote-user";
  private static final String OS_URL = "social/rest/people/" + USER_ID;
  private final static String OAUTH_OPENCONEXT_API_READ_SCOPE = "read";

  @Test
  public void noAccessWithoutToken() {
    getWebDriver().manage().deleteAllCookies();
    getWebDriver().get(getApiBaseUrl() + OS_URL);
    final String pageSource = getWebDriver().getPageSource();
    LOG.debug("Response body: {}", pageSource);
    assertFalse("No valid content without an OAuth token", pageSource.contains("@example.com"));
  }

  @Test
  public void test() {
    OAuthService service = new ServiceBuilder()
        .provider(new OpenConextApi10aThreeLegged(getApiBaseUrl()))
        .apiKey(OAUTH_KEY)
        .apiSecret(OAUTH_SECRET)
        .scope(OAUTH_OPENCONEXT_API_READ_SCOPE)
        .callback("oob")
        .signatureType(SignatureType.QueryString)
        .debug()
        .build();
    Token requestToken = service.getRequestToken();
    LOG.debug("Request token: {}", requestToken);
    assertNotNull(requestToken);

    String authUrl = service.getAuthorizationUrl(requestToken);
    LOG.debug("Authorization url: {}", authUrl);
    assertNotNull(authUrl);

    // direct user to verification url.
    getWebDriver().get(authUrl);
    LOG.debug("Confirm-URL: {}", getWebDriver().getCurrentUrl());
    getWebDriver().findElement(By.id("accept_terms_button")).click();
    LOG.debug("after-Confirm-URL: {}", getWebDriver().getCurrentUrl());

    final String redirectUrl = getWebDriver().getCurrentUrl();
    String verifier = redirectUrl.substring(redirectUrl.indexOf("oauth_verifier=") + 15);
    LOG.debug("Verifier: {}", verifier);

    Token accessToken = service.getAccessToken(requestToken, new Verifier(verifier));
    assertNotNull(accessToken);
    LOG.debug("Access token: {}", accessToken);

    OAuthRequest req = new OAuthRequest(Verb.GET, getApiBaseUrl() + OS_URL);
    service.signRequest(accessToken, req);
    LOG.debug("Signed resource request: {}", req.toString());

    Response response = req.send();
    String bodyText = response.getBody();
    LOG.debug("Response body: {}", bodyText);
    assertTrue("response body should contain correct json data", bodyText.contains("mock-shib-remote-user"));
    
    //test the acces token (without cookies)
    req = new OAuthRequest(Verb.GET, getApiBaseUrl() + OS_URL);
    service.signRequest(accessToken, req);
    LOG.debug("Signed resource request: {}", req.toString());

    response = req.send();
    bodyText = response.getBody();
    LOG.debug("Response body: {}", bodyText);
    assertTrue("response body should contain correct json data", bodyText.contains("mock-shib-remote-user"));
    
    //also test the mock 
    
    req = new OAuthRequest(Verb.GET, (getApiBaseUrl() + OS_URL).replace("/social/rest/", "/mock10/social/rest/"));
    service.signRequest(accessToken, req);
    LOG.debug("Signed resource request: {}", req.toString());

    response = req.send();
    bodyText = response.getBody();
    LOG.debug("Response body: {}", bodyText);
    assertTrue(bodyText.contains("mnice@surfguest.nl"));

  }
}