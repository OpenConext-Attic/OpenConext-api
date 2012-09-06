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
package nl.surfnet.coin.api.playground;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.scribe.model.Token;
import org.scribe.model.Verb;

/**
 * Backing form object for all OAuth calls from the Test Framework
 * 
 */
public class ApiSettings {

  private String version = OAuthVersion.VERSION10A.getVersion();// "1.0a"
  private String requestTokenEndPoint;
  private String requestTokenVerb = Verb.POST.toString();
  private String accessTokenEndPoint;
  private String accessTokenEndPoint2;
  private boolean twoLegged;
  private boolean implicitGrant;
  private boolean leaveOutRedirectUri;
  private String scope = "read";

  private String oauthKey;
  private String oauthSecret;
  private String authorizationURL;
  private String authorizationURL2;
  private String step;
  private Token requestToken;

  private boolean queryParameters;
  private String accessTokenRequestOption = AccessTokenRequestOption.ENTITY_BODY_PARAMETERS.getOption();// "Entity Body Parameters"

  private String requestURL;
  private int count;
  private int startIndex;
  private String sortBy;
  private String parseAnchorForAccesstoken;

  public ApiSettings() {
    this("dev");
  }

  /**
   * @param string
   */
  public ApiSettings(String url) {
    String env;
    try {
      Pattern pattern = Pattern.compile("api\\.(\\w*)\\.surfconext");
      Matcher matcher = pattern.matcher(url);

      env = (matcher.find() ? ("." + matcher.group(1) + ".") : ".");
    } catch (Exception e) {
      //unexpected, but we won't risk runtime errors for defaults
      env = ".";
    }

    this.oauthKey = String.format("https://testsp%ssurfconext.nl/shibboleth", env);
    this.oauthSecret = "mysecret";

    this.requestTokenEndPoint = String.format("https://api%ssurfconext.nl/v1/oauth1/requestToken", env);
    this.accessTokenEndPoint = String.format("https://api%ssurfconext.nl/v1/oauth1/accessToken", env);
    this.authorizationURL = String.format("https://api%ssurfconext.nl/v1/oauth1/confirm_access", env);

    this.accessTokenEndPoint2 = String.format("https://api%ssurfconext.nl/v1/oauth2/token", env);
    this.authorizationURL2 = String.format("https://api%ssurfconext.nl/v1/oauth2/authorize", env);

    this.requestURL = String.format("https://api%ssurfconext.nl/v1/social/rest/groups/@me", env);

  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public Token getRequestToken() {
    return requestToken;
  }

  public void setRequestToken(Token requestToken) {
    this.requestToken = requestToken;
  }

  public String getStep() {
    return step;
  }

  public void setStep(String step) {
    this.step = step;
  }

  public String getAuthorizationURL() {
    return authorizationURL;
  }

  public void setAuthorizationURL(String authorizationURL) {
    this.authorizationURL = authorizationURL;
  }

  public String getOauthKey() {
    return oauthKey;
  }

  public void setOauthKey(String oauthKey) {
    this.oauthKey = oauthKey;
  }

  public String getOauthSecret() {
    return oauthSecret;
  }

  public void setOauthSecret(String oauthSecret) {
    this.oauthSecret = oauthSecret;
  }

  public boolean isTwoLegged() {
    return twoLegged;
  }

  public boolean isTwoLeggedOauth() {
    return twoLegged && isOAuth10a();
  }

  public boolean isOAuth10a() {
    return OAuthVersion.VERSION10A.getVersion().equals(getVersion());
  }

  public void setTwoLegged(boolean twoLegged) {
    this.twoLegged = twoLegged;
  }

  public String getRequestTokenEndPoint() {
    return requestTokenEndPoint;
  }

  public void setRequestTokenEndPoint(String requestTokenEndPoint) {
    this.requestTokenEndPoint = requestTokenEndPoint;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getAccessTokenEndPoint() {
    return accessTokenEndPoint;
  }

  public void setAccessTokenEndPoint(String accessTokenEndPoint) {
    this.accessTokenEndPoint = accessTokenEndPoint;
  }

  public String getAccessTokenEndPoint2() {
    return accessTokenEndPoint2;
  }

  public void setAccessTokenEndPoint2(String accessTokenEndPoint2) {
    this.accessTokenEndPoint2 = accessTokenEndPoint2;
  }

  public boolean isImplicitGrant() {
    return implicitGrant;
  }

  public boolean isImplicitGrantOauth() {
    return implicitGrant && !isOAuth10a();
  }

  public void setImplicitGrant(boolean implicitGrant) {
    this.implicitGrant = implicitGrant;
  }

  public String getAuthorizationURL2() {
    return authorizationURL2;
  }

  public void setAuthorizationURL2(String authorizationURL2) {
    this.authorizationURL2 = authorizationURL2;
  }

  public String getRequestURL() {
    return requestURL;
  }

  public void setRequestURL(String requestURL) {
    this.requestURL = requestURL;
  }

  public String getParseAnchorForAccesstoken() {
    return parseAnchorForAccesstoken;
  }

  public void setParseAnchorForAccesstoken(String parseAnchorForAccesstoken) {
    this.parseAnchorForAccesstoken = parseAnchorForAccesstoken;
  }

  /**
   * @return the queryParameters
   */
  public boolean isQueryParameters() {
    return queryParameters;
  }

  /**
   * @param queryParameters
   *          the queryParameters to set
   */
  public void setQueryParameters(boolean queryParameters) {
    this.queryParameters = queryParameters;
  }

  /**
   * @return the accessTokenRequestOption
   */
  public String getAccessTokenRequestOption() {
    return accessTokenRequestOption;
  }

  /**
   * @param accessTokenRequestOption
   *          the accessTokenRequestOption to set
   */
  public void setAccessTokenRequestOption(String accessTokenRequestOption) {
    this.accessTokenRequestOption = accessTokenRequestOption;
  }

  /**
   * @return the requestTokenVerb
   */
  public String getRequestTokenVerb() {
    return requestTokenVerb;
  }

  /**
   * @param requestTokenVerb
   *          the requestTokenVerb to set
   */
  public void setRequestTokenVerb(String requestTokenVerb) {
    this.requestTokenVerb = requestTokenVerb;
  }

  /**
   * @return the scope
   */
  public String getScope() {
    return scope;
  }

  /**
   * @param scope
   *          the scope to set
   */
  public void setScope(String scope) {
    this.scope = scope;
  }

  /**
   * @return the leaveOutRedirectUri
   */
  public boolean isLeaveOutRedirectUri() {
    return leaveOutRedirectUri;
  }

  /**
   * @param leaveOutRedirectUri
   *          the leaveOutRedirectUri to set
   */
  public void setLeaveOutRedirectUri(boolean leaveOutRedirectUri) {
    this.leaveOutRedirectUri = leaveOutRedirectUri;
  }

}
