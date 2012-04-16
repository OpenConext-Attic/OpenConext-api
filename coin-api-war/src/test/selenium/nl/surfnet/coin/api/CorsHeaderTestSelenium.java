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

import org.junit.Before;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.surfnet.coin.api.client.OpenConextApi10aTwoLegged;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CorsHeaderTestSelenium {

  private static final String OAUTH_KEY = "https://testsp.test.surfconext.nl/shibboleth";
  private static final String OAUTH_SECRET = "mysecret";

  private static final String USER_ID = "urn:collab:person:test.surfguest.nl:oharsta";
  private static final String OS_URL = "social/rest/people/" + USER_ID + "/@self";
  private final static String OAUTH_OPENCONEXT_API_READ_SCOPE = "read";

  private String getApiBaseUrl() {
    return System.getProperty("selenium.test.url", "http://localhost:8095/");
  }

  @Test
  public void corsHeader() throws Exception {
    OAuthService service = new ServiceBuilder()
        .provider(new OpenConextApi10aTwoLegged())
        .apiKey(OAUTH_KEY)
        .apiSecret(OAUTH_SECRET)
        .scope(OAUTH_OPENCONEXT_API_READ_SCOPE)
        .callback("oob")
        .signatureType(SignatureType.QueryString)
        .debug()
        .build();

    OAuthRequest req = new OAuthRequest(Verb.GET, getApiBaseUrl() + OS_URL);
    service.signRequest(new Token("", ""), req);
    Response response = req.send();
    assertNotNull("Response should contain CORS-header", response.getHeaders().get("Access-Control-Allow-Origin"));
    assertTrue("CORS-header should be '*'", response.getHeaders().get("Access-Control-Allow-Origin").equals("*"));
  }
}
