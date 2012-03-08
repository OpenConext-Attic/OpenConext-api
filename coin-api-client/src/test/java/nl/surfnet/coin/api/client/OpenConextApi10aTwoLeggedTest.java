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
package nl.surfnet.coin.api.client;

import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * 
 *
 */
public class OpenConextApi10aTwoLeggedTest  {
  // http://devlog.bafford.us/two-legged-oauth-in-java-using-scribe-to-acce

  private final String OAUTH_KEY = "https://testsp.test.surfconext.nl/test";
  private final String OAUTH_SECRET = "mysecret";

  // private final String OAUTH_CALLBACK_URL =
  // "http://localhost:8080/java-oauth-example/home.shtml";
 // private final String SURFCONEXT_BASE_URL = "https://os.test.surfconext.nl/";
  private final String SURFCONEXT_BASE_URL = "http://localhost:8083/";

  private final String USER_ID = "urn:collab:person:test.surfguest.nl:oharsta";

  @Test
  public void testTwoLeggedOauth() throws Exception {
    OAuthService service = new ServiceBuilder()
        .provider(OpenConextApi10aTwoLegged.class).apiKey(OAUTH_KEY)
        .apiSecret(OAUTH_SECRET).build();

    // for 3-legged you would need to request the authorization token
    // OpenGeo is a two-legged OAuth server, so the token is empty
    Token token = new Token("", "");
    OAuthRequest request = new OAuthRequest(Verb.GET, SURFCONEXT_BASE_URL
        + "social/rest/people/" + USER_ID + "/@self");

    service.signRequest(token, request);
    Response response = request.send();

    System.out.println("Response: " + response.getBody());
  }
}
