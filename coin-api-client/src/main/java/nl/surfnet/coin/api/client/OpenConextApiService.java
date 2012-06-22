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

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;

/**
 * Abstraction of the OAuth shebang of api.surfconext.
 * <p>
 * Call init() after setting properties, before calling any service methods.
 * </p>
 */
public class OpenConextApiService {

  private URI endpointBaseUrl;
  private String oauthConsumerKey;
  private String oauthConsumerSecret;


  private OpenConextOAuthClient oauthClient;
  private boolean initialized;
  private OAuthEnvironment environment;
  private OAuthRepository repository;


  private OAuthVersion version;

  public void init() {
    environment = new OAuthEnvironment();
    environment.setEndpointBaseUrl(endpointBaseUrl.toString());
    environment.setOauthKey(oauthConsumerKey);

    environment.setOauthSecret(oauthConsumerSecret);
    repository = new InMemoryOAuthRepositoryImpl();
    oauthClient = new OpenConextOAuthClientImpl(environment, repository);
    initialized = true;
  }

  public void setCallbackUrl(String url) {
    environment.setCallbackUrl(url);
  }
  public String getAuthorizationUrl() {
    return oauthClient.getAuthorizationUrl(version);
  }

  public Person getPerson(String userId, String onBehalfOf) {
    assertInitialized();
    return oauthClient.getPerson(userId, onBehalfOf);
  }

  public List<Person> getGroupMembers(String groupId, String onBehalfOf) {
    assertInitialized();
    return oauthClient.getGroupMembers(groupId, onBehalfOf);
  }

  public List<Group> getGroups(String userId, String onBehalfOf) {
    assertInitialized();
    return oauthClient.getGroups(userId, onBehalfOf);
  }

  public List<Group20> getGroups20(String userId, String onBehalfOf) {
    assertInitialized();
    return oauthClient.getGroups20(userId, onBehalfOf);
  }

  public void setEndpointBaseUrl(URI endpointBaseUrl) {
    this.endpointBaseUrl = endpointBaseUrl;
  }

  public void setOauthConsumerKey(String oauthConsumerKey) {
    this.oauthConsumerKey = oauthConsumerKey;
  }

  public void setOauthConsumerSecret(String oauthConsumerSecret) {
    this.oauthConsumerSecret = oauthConsumerSecret;
  }

  private void assertInitialized() {
    if (!initialized) {
      throw new RuntimeException("OpenConextApiService not initialized yet. Call init() prior to service methods.");
    }
  }

  public boolean isAuthorized(String userId) {
    return (oauthClient.isAccessTokenGranted(userId));
  }

  public void oauthCallback(HttpServletRequest request, String onBehalfOf) {
    oauthClient.oauthCallback(version, request, onBehalfOf);
  }

  public void setVersion(OAuthVersion version) {
    this.version = version;
  }
}
