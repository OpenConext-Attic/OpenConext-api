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

package nl.surfnet.coin.teams.domain;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.scribe.model.Token;
import org.scribe.model.Verb;

import nl.surfnet.coin.teams.util.GroupProviderOptionParameters;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Test class for {@link ThreeLeggedOauth10aGroupProviderApi}
 */
public class ThreeLeggedOauth10aGroupProviderApiTest {
  GroupProvider groupProvider;

  @Before
  public void setup() {
    groupProvider = new GroupProvider(1L, "gp", "gp", GroupProviderType.OAUTH_THREELEGGED.getStringValue());
  }

  @Test
  public void testHappyFlow() {
    Map<String, Object> options = new HashMap<String, Object>();
    String requestMethod = "get";
    options.put(GroupProviderOptionParameters.REQUEST_METHOD, requestMethod);
    String reqTokenEndpoint = "https://example.com/requesttoken";
    options.put(GroupProviderOptionParameters.REQUEST_TOKEN_URL, reqTokenEndpoint);
    String accTokenEndpoint = "https://example.com/accesstoken";
    options.put(GroupProviderOptionParameters.ACCESS_TOKEN_URL, accTokenEndpoint);
    String authUrl = "https://example.com/authurl";

    options.put(GroupProviderOptionParameters.AUTHORIZE_URL, authUrl);
    groupProvider.setAllowedOptions(options);

    ThreeLeggedOauth10aGroupProviderApi api =
        new ThreeLeggedOauth10aGroupProviderApi(groupProvider);

    assertEquals(Verb.GET, api.getAccessTokenVerb());
    assertEquals(Verb.GET, api.getRequestTokenVerb());
    assertEquals(reqTokenEndpoint, api.getRequestTokenEndpoint());
    assertEquals(accTokenEndpoint, api.getAccessTokenEndpoint());
    Token token = new Token("mytoken", "secret");
    assertEquals(authUrl + "?oauth_token=mytoken", api.getAuthorizationUrl(token));
  }

  @Test
  public void testAccessTokenVerb_empty() throws Exception {
    ThreeLeggedOauth10aGroupProviderApi api =
        new ThreeLeggedOauth10aGroupProviderApi(groupProvider);
    assertEquals(Verb.POST, api.getAccessTokenVerb());
  }

  @Test
  public void testAccessTokenVerb_unknownValue() throws Exception {
    Map<String, Object> options = new HashMap<String, Object>();
    String requestMethod = "BREW"; // RFC2324
    options.put(GroupProviderOptionParameters.REQUEST_METHOD, requestMethod);

    ThreeLeggedOauth10aGroupProviderApi api =
        new ThreeLeggedOauth10aGroupProviderApi(groupProvider);

    assertEquals(Verb.POST, api.getAccessTokenVerb());
  }

  @Test
  public void testRequestTokenVerb_empty() throws Exception {
    ThreeLeggedOauth10aGroupProviderApi api =
        new ThreeLeggedOauth10aGroupProviderApi(groupProvider);
    assertEquals(Verb.POST, api.getRequestTokenVerb());
  }

  @Test
  public void testGetRequestTokenEndpoint_empty() throws Exception {
    ThreeLeggedOauth10aGroupProviderApi api =
        new ThreeLeggedOauth10aGroupProviderApi(groupProvider);
    assertNull(api.getRequestTokenEndpoint());
  }

  @Test
  public void testGetAccessTokenEndpoint_empty() throws Exception {
    ThreeLeggedOauth10aGroupProviderApi api =
        new ThreeLeggedOauth10aGroupProviderApi(groupProvider);
    assertNull(api.getAccessTokenEndpoint());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetAuthorizationUrl_empty() throws Exception {
    ThreeLeggedOauth10aGroupProviderApi api =
        new ThreeLeggedOauth10aGroupProviderApi(groupProvider);
    Token token = new Token("mytoken", "secret");
    api.getAuthorizationUrl(token);
  }
}
