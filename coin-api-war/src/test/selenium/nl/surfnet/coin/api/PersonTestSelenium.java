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
import org.springframework.core.io.ByteArrayResource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.System;
import java.lang.Thread;
import java.net.URLDecoder;

/**
 * Test Person related queries with selenium
 * 
 */
public class PersonTestSelenium extends SeleniumSupport {
    private final String OAUTH_KEY = "https://testsp.test.surfconext.nl/test";
    private final String OAUTH_SECRET = "mysecret";

    // private final String OAUTH_CALLBACK_URL =
    // "http://localhost:8080/java-oauth-example/home.shtml";
    // private final String SURFCONEXT_BASE_URL = "https://os.test.surfconext.nl/";
    private final String SURFCONEXT_BASE_URL = "http://localhost:8095/";

    private final String USER_ID = "urn:collab:person:test.surfguest.nl:oharsta";
    private MockHtppServer server;


    @Test
    public void completeFlow() throws Exception {

        OAuthService service = new ServiceBuilder()
                .provider(OpenConextApi20ThreeLegged.class)
                .apiKey(OAUTH_KEY)
                .apiSecret(OAUTH_SECRET)
                .callback("http://localhost:8083/")
                .build();
        String authUrl = service.getAuthorizationUrl(null);
        System.out.println("Auth url: " + authUrl);


        getWebDriver().get(authUrl);
        // log in...
        getWebDriver().findElementByName("j_username").sendKeys("bob");
        getWebDriver().findElementByName("j_password").sendKeys("bobspassword");
        getWebDriver().findElementByName("submit").click();
        // TODO: follow redirect to redirect_url...
        while (verifier == null) {
            Thread.sleep(10L);
        }
        System.out.println("Verifier is not null anymore: " + verifier);
        Token aToken = service.getAccessToken(null, verifier);

        OAuthRequest request = new OAuthRequest(Verb.GET, SURFCONEXT_BASE_URL
                + "rest/people/" + USER_ID + "/@self");

        service.signRequest(aToken, request);
        Response response = request.send();

        System.out.println("Response: " + response.getBody());
        
        
        
        // scribe client: authorization URL (/oauth/authorize?client_id=....&redirect_url=http://local:9999/...)

        // start http server (local:9999)

        // selenium: conversation with authorization url + redirect to local:9999/?request_token=2345678

        // scribe: use access token: getPerson

    }

    private Verifier verifier;

    @After
    public void stopServer() {
        System.out.println("Stopping server...");
        server.stopServer();
    }
    @Before
    public void startServer() {
        System.out.println("Starting server...");
        server = new MockHtppServer(8083) {
            protected MockHandler createHandler(Server server) {
                return new MockHandler(server) {

                    @Override
                    public void  handle(String target, Request baseRequest, HttpServletRequest request,
                                        HttpServletResponse response) throws IOException,
                            ServletException {

                        System.out.println(request.toString());
                        verifier = new Verifier(request.getParameter("code"));
//                        userId = URLDecoder.decode(userId, "UTF-8");
                        response.setStatus(200);
                    }
                };
            }
        };
        server.startServer();
    }
}
