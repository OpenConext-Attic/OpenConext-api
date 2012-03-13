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

package nl.surfnet.coin.api.client;

import org.junit.Ignore;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

import java.util.Scanner;

public class OpenConextApi20ThreeLeggedTest {

    // http://devlog.bafford.us/two-legged-oauth-in-java-using-scribe-to-acce

    private final String OAUTH_KEY = "https://testsp.test.surfconext.nl/test";
    private final String OAUTH_SECRET = "mysecret";

    // private final String OAUTH_CALLBACK_URL =
    // "http://localhost:8080/java-oauth-example/home.shtml";
    // private final String SURFCONEXT_BASE_URL = "https://os.test.surfconext.nl/";
    private final String SURFCONEXT_BASE_URL = "http://localhost:8099/";

    private final String USER_ID = "urn:collab:person:test.surfguest.nl:oharsta";
    private static final Token EMPTY_TOKEN = null;
    @Ignore

    @Test
    public void successFlow() throws Exception {
        OAuthService service = new ServiceBuilder()
                .provider(OpenConextApi20ThreeLegged.class)
                .apiKey(OAUTH_KEY)
                .apiSecret(OAUTH_SECRET)
//                .callback("http://foobar")
                .build();
        String authUrl = service.getAuthorizationUrl(null);
        System.out.println("Auth url: " + authUrl);
        Verifier verifier = new Verifier("verifier you got from the user");
        Token aToken = service.getAccessToken(null, verifier);

        OAuthRequest request = new OAuthRequest(Verb.GET, SURFCONEXT_BASE_URL
                + "rest/people/" + USER_ID + "/@self");

        service.signRequest(aToken, request);
        Response response = request.send();

        System.out.println("Response: " + response.getBody());
    }
    @Ignore

    @Test
    public void testFB() {
        // Replace these with your own api key and secret
        String apiKey = OAUTH_KEY;
        String apiSecret = OAUTH_SECRET;
        OAuthService service = new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback("http://www.example.com/oauth_callback/")
                .build();
        Scanner in = new Scanner(System.in);

        System.out.println("=== OAuth Workflow ===");
        System.out.println();

        // Obtain the Authorization URL
        System.out.println("Fetching the Authorization URL...");
        String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize Scribe here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        Verifier verifier = new Verifier(in.nextLine());
        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        OAuthRequest request = new OAuthRequest(Verb.GET, SURFCONEXT_BASE_URL
                + "rest/people/" + USER_ID + "/@self");
        service.signRequest(accessToken, request);
        Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getBody());

        System.out.println();
        System.out.println("Thats it man! Go and build something awesome with Scribe! :)");


    }
}
