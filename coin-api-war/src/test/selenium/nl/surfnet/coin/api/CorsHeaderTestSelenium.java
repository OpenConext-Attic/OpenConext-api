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

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import nl.surfnet.coin.api.client.OpenConextApi10aTwoLegged;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.containsString;

public class CorsHeaderTestSelenium {

  private static final String OAUTH_KEY = "https://testsp.dev.surfconext.nl/shibboleth";
  private static final String OAUTH_SECRET = "mysecret";

  private static final String USER_ID = "urn:collab:person:test.surfguest.nl:oharsta";
  private static final String OS_URL = "social/rest/people/" + USER_ID + "/@self";
  private final static String OAUTH_OPENCONEXT_API_READ_SCOPE = "read";

  private String getApiBaseUrl() {
    return System.getProperty("selenium.test.url", "http://localhost:8095/");
  }

  @Test
  public void preflight() throws Exception {
    HttpClient client = new DefaultHttpClient();
    HttpUriRequest req = new HttpOptions(getApiBaseUrl() + OS_URL);
    req.setHeader("Origin", "localhost");
    client.execute(req, new ResponseHandler<Object>() {
      @Override
      public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        System.out.println("Response of preflight request: " + response.toString());
        assertThat("response header Access-Control-Allow-Methods should contain 'GET'",
            response.getFirstHeader("Access-Control-Allow-Methods").getValue(), containsString("GET"));
        assertThat("No content should be served on a preflight request", response.getEntity().getContentLength(),
            equalTo(0L));
        return null;
      }
    });
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
