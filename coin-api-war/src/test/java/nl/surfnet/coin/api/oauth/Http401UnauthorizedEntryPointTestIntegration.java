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
package nl.surfnet.coin.api.oauth;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

/**
 * Http401UnauthorizedEntryPointTestIntegration.java
 * 
 */
public class Http401UnauthorizedEntryPointTestIntegration {

  private static final String API_URL = "social/rest/people/groups/@me";

  private String getApiBaseUrl() {
    return System.getProperty("selenium.test.url", "http://localhost:8095/api/");
  }

  @Test
  public void authenticationHeader() throws Exception {
    HttpClient client = new DefaultHttpClient();
    HttpUriRequest req = new HttpGet(getApiBaseUrl() + API_URL);
    HttpResponse res = client.execute(req);
    Header header = res.getFirstHeader("WWW-Authenticate");
    assertEquals("Bearer realm=\"api.surfconext\"", header.getValue());

  }

}
