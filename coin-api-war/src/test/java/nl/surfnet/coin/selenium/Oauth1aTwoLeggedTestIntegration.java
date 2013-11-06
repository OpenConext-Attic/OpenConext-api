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

import nl.surfnet.coin.api.client.internal.OpenConextApi20ClientCredentials;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Oauth1aTwoLeggedTestIntegration extends SeleniumSupport {

  private Logger LOG = LoggerFactory.getLogger(Oauth1aTwoLeggedTestIntegration.class);

  private static final String OAUTH_KEY = "https://testsp.test.surfconext.nl/shibboleth";
  private static final String OAUTH_SECRET = "mysecret";

  private static final String USER_ID = "mock-shib-remote-user";
  private static final String OS_URL = "social/rest/people/" + USER_ID;
  private final static String OAUTH_OPENCONEXT_API_READ_SCOPE = "read";


  @Test
  public void withoutToken() {

    // Use a request that is not signed.
    OAuthRequest req = new OAuthRequest(Verb.GET, getApiBaseUrl() + OS_URL);
    Response response = req.send();
    final String bodyText = response.getBody();
    LOG.debug("Response body: {}", bodyText);
    assertFalse("response body should not contain json data", bodyText.contains("Mister Nice"));
  }



  @Test
  public void withToken() {
    OAuthService service = new ServiceBuilder()
        .provider(new OpenConextApi20ClientCredentials())
        .apiKey(OAUTH_KEY)
        .apiSecret(OAUTH_SECRET)
        .scope(OAUTH_OPENCONEXT_API_READ_SCOPE)
        .callback("oob")
        .signatureType(SignatureType.QueryString)
        .debug()
        .build();

    OAuthRequest req = new OAuthRequest(Verb.GET, getApiBaseUrl() + OS_URL);
    Token accessToken = service.getAccessToken(new Token("", ""), new Verifier(""));
    service.signRequest(accessToken, req);
    Response response = req.send();
    final String bodyText = response.getBody();
    LOG.debug("Response body: {}", bodyText);
    assertTrue("response body should contain correct json data", bodyText.contains("\"id\":\"mock-shib-remote-user\""));
  }
}
