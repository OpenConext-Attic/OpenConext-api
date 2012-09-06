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

package nl.surfnet.coin.api.playground;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.ParameterList;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * Controller for all OAuth client calls
 */
@Controller
public class OAuthClientController {

  @Value("${coin-api.oauth.callback.url}")
  private String oauthCallbackUrl;

  @Autowired
  private StringBuffer versionIdentifier;

  private final Token EMPTY_TOKEN = new Token("", "");

  @RequestMapping(value = { "/test" }, method = RequestMethod.GET)
  public String socialQueries(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws IOException {
    setupModelMap(new ApiSettings(), "step1", request, modelMap, null);
    modelMap.addAttribute("versionIdentifier", versionIdentifier);
    return "oauth-client";
  }

  @RequestMapping(value = "/test", method = RequestMethod.POST, params = "step1")
  public String step1(ModelMap modelMap, @ModelAttribute("settings") ApiSettings settings, HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    if (settings.isTwoLeggedOauth()) {
      settings.setAccessTokenEndPoint("");
      settings.setAuthorizationURL("");
      settings.setRequestTokenEndPoint("");
    }
    OAuthService service;
    if (settings.isOAuth10a()) {
      service = getService10(settings);// ConfigurableOAuth10aServiceImpl
    } else {
      service = getService20(settings);// ConfigurableOAuth20ServiceImpl
    }
    if (settings.isTwoLeggedOauth()) {
      // we go straight to step 3
      settings.setRequestToken(EMPTY_TOKEN);
      request.getSession().setAttribute("accessToken", EMPTY_TOKEN);
      setupModelMap(settings, "step3", request, modelMap, service);
      return "oauth-client";
    }
    String authorizationUrl;
    if (settings.isOAuth10a()) {
      ConfigurableOAuth10aServiceImpl service10a = (ConfigurableOAuth10aServiceImpl) service;
      OAuthRequest oAuthRequest = service10a.getOAuthRequest(settings.getRequestTokenVerb());
      Response oauthResponse = service10a.getOauthResponse(oAuthRequest);
      Token requestToken = service10a.getRequestToken(oauthResponse);
      authorizationUrl = service.getAuthorizationUrl(requestToken);
      settings.setRequestToken(requestToken);
      modelMap.addAttribute("requestInfo", oAuthRequestToString(oAuthRequest));
      modelMap.addAttribute("responseInfo", oAuthResponseHeadersToString(oauthResponse));
      modelMap.addAttribute("rawResponseInfo", oAuthResponseBodyToString(oauthResponse));
    } else {
      ConfigurableOAuth20ServiceImpl service20 = (ConfigurableOAuth20ServiceImpl) service;
      authorizationUrl = service20.getAuthorizationUrl(EMPTY_TOKEN);
      if (settings.isLeaveOutRedirectUri()) {
        authorizationUrl = authorizationUrl.replaceFirst("&redirect_uri=oob", "");
      }
    }
    modelMap.addAttribute("authorizationUrlAfter", authorizationUrl);
    setupModelMap(settings, "step2", request, modelMap, service);
    return "oauth-client";
  }

  private static String oAuthRequestToString(OAuthRequest request) {
    ParameterList bodyParams = request.getBodyParams();
    ParameterList queryStringParams = request.getQueryStringParams();
    Map<String, String> headers = request.getHeaders();
    String br = System.getProperty("line.separator");
    return String.format(
        "%s %s %s %s %s",
        "METHOD: ".concat(request.getVerb().toString()).concat(br),
        "URL: ".concat(request.getUrl().toString()).concat(br),
        (bodyParams != null && bodyParams.size() > 0) ? "BODY: ".concat(bodyParams.asFormUrlEncodedString()).concat(br) : "",
        (queryStringParams != null && queryStringParams.size() > 0) ? "QUERY: ".concat(queryStringParams.asFormUrlEncodedString()).concat(
            br) : "", (headers != null && !headers.isEmpty() ? "HEADERS: ".concat(headers.toString()) : ""));
  }

  private static String oAuthResponseHeadersToString(Object response) {
    if (response instanceof Response) {
      Response realResponse = (Response) response;
      return String.format("%s, %s", realResponse.getCode(), realResponse.getHeaders());
    }
    return response.toString();
  }

  private static String oAuthResponseBodyToString(Object response) {
    if (response instanceof Response) {
      Response realResponse = (Response) response;
      return realResponse.getBody();
    }
    return response.toString();
  }

  @RequestMapping(value = "/test", method = RequestMethod.POST, params = "step2")
  public void step2(ModelMap modelMap, @ModelAttribute("settings") ApiSettings settings, HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    ApiSettings settingsFromSession = (ApiSettings) request.getSession().getAttribute("settings");
    String authorizationUrl;
    OAuthService service = (OAuthService) request.getSession().getAttribute("service");
    if (settingsFromSession.isOAuth10a()) {
      ConfigurableOAuth10aServiceImpl service10a = (ConfigurableOAuth10aServiceImpl) service;
      Token requestToken = (Token) settingsFromSession.getRequestToken();
      authorizationUrl = service10a.getAuthorizationUrl(requestToken);
    } else {
      ConfigurableOAuth20ServiceImpl service20 = (ConfigurableOAuth20ServiceImpl) service;
      authorizationUrl = service20.getAuthorizationUrl(EMPTY_TOKEN);
      if (settings.isLeaveOutRedirectUri()) {
        authorizationUrl = authorizationUrl.replaceFirst("&redirect_uri=oob", "");
      }
      settingsFromSession.setAccessTokenRequestOption(settings.getAccessTokenRequestOption());
      request.getSession().setAttribute("settings", settingsFromSession);
    }
    response.sendRedirect(authorizationUrl);
  }

  @RequestMapping(value = "/test", method = RequestMethod.POST, params = "step3")
  public String step3(ModelMap modelMap, @ModelAttribute("settings") ApiSettings settings, HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    OAuthService service = (OAuthService) request.getSession().getAttribute("service");// getService10(settings);
    Token accessToken = (Token) request.getSession().getAttribute("accessToken");
    StringBuffer requestURL = new StringBuffer(settings.getRequestURL() + "?");
    if (settings.getCount() != 0) {
      requestURL.append("count=" + settings.getCount() + "&");
    }
    if (settings.getStartIndex() != 0) {
      requestURL.append("startIndex=" + settings.getStartIndex() + "&");
    }
    if (StringUtils.hasText(settings.getSortBy())) {
      requestURL.append("sortBy=" + settings.getSortBy() + "&");
    }
    OAuthRequest oAuthRequest = new OAuthRequest(Verb.GET, requestURL.toString());
    if (service instanceof ConfigurableOAuth20ServiceImpl) { // &&
                                                             // !settings.isSignWithQueryParameter())
                                                             // {
      ConfigurableOAuth20ServiceImpl service20 = (ConfigurableOAuth20ServiceImpl) service;
      service20.signRequestAsHeader(accessToken, oAuthRequest);
    } else {
      service.signRequest(accessToken, oAuthRequest);
    }
    long time = System.currentTimeMillis();
    Response oAuthResponse = oAuthRequest.send();
    modelMap.addAttribute("responseTime", String.format("(Took %s ms)", (System.currentTimeMillis() - time)));
    modelMap.addAttribute("requestInfo", oAuthRequestToString(oAuthRequest));
    modelMap.addAttribute("responseInfo", oAuthResponseHeadersToString(oAuthResponse));
    modelMap.addAttribute("rawResponseInfo", oAuthResponseBodyToString(oAuthResponse));
    modelMap.addAttribute("accessToken", accessToken);
    setupModelMap(settings, "step3", request, modelMap, service);
    return "oauth-client";

  }

  @RequestMapping(value = "/test", method = RequestMethod.POST, params = "reset")
  public String reset(ModelMap modelMap, @ModelAttribute("settings") ApiSettings settings, HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    return socialQueries(modelMap, request, response);
  }

  @RequestMapping(value = "/test/parseAnchor.shtml", method = RequestMethod.GET)
  @ResponseBody
  public String parseAnchor(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String token = request.getParameter("access_token");
    Token accessToken = new Token(token, "");
    request.getSession().setAttribute("accessToken", accessToken);
    return request.getQueryString();
  }

  @RequestMapping(value = "/test/oauth-callback.shtml", method = RequestMethod.GET)
  public String oauthCallBack(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    ApiSettings settings = (ApiSettings) request.getSession().getAttribute("settings");
    String verifierParam = (settings.isOAuth10a() ? "oauth_verifier" : "code");
    String oAuthVerifier = request.getParameter(verifierParam);
    OAuthService service = (OAuthService) request.getSession().getAttribute("service");
    Token accessToken;
    OAuthRequest oAuthRequest;
    Object oauthResponse;
    if (settings.isImplicitGrantOauth()) {
      // we will extract the token (which is in the anchor of the url and not
      // accessible here) on the client
      accessToken = EMPTY_TOKEN;
      oAuthRequest = new OAuthRequest(Verb.GET, request.getRequestURL().append("?").append(request.getQueryString()).toString());
      oauthResponse = "";
      modelMap.addAttribute("parseAnchorForAccesstoken", Boolean.TRUE);
    } else {
      Verifier verifier = new Verifier(oAuthVerifier);
      Token requestToken = settings.getRequestToken();
      if (requestToken == null) {
        requestToken = EMPTY_TOKEN;
      }
      if (settings.isOAuth10a()) {
        ConfigurableOAuth10aServiceImpl service10a = (ConfigurableOAuth10aServiceImpl) service;
        oAuthRequest = service10a.getAccessTokenRequest(requestToken, verifier);
        oauthResponse = service10a.getAccessTokenResponse(oAuthRequest);
        accessToken = service10a.getAccessTokenFromResponse((Response) oauthResponse);
      } else {
        ConfigurableOAuth20ServiceImpl service20 = (ConfigurableOAuth20ServiceImpl) service;
        switch (AccessTokenRequestOption.valueOfOption(settings.getAccessTokenRequestOption())) {
        case AUTHENTICATION_HEADER: {
          oAuthRequest = service20.getOAuthRequestConformSpec(verifier);
          break;
        }
        case QUERY_STRING_PARAMETERS: {
          oAuthRequest = service20.getOAuthRequest(verifier, true);
          break;
        }
        case ENTITY_BODY_PARAMETERS: {
          oAuthRequest = service20.getOAuthRequest(verifier, false);
          break;
        }
        default: {
          throw new RuntimeException(String.format("Unable to determine the AccessTokenRequestOption %s",
              settings.getAccessTokenRequestOption()));
        }
        }
        oauthResponse = service20.getOauthResponse(oAuthRequest);
        accessToken = service20.getAccessToken((Response) oauthResponse);
      }
    }

    request.getSession().setAttribute("accessToken", accessToken);
    modelMap.addAttribute("requestInfo", oAuthRequestToString(oAuthRequest));
    modelMap.addAttribute("responseInfo", oAuthResponseHeadersToString(oauthResponse));
    modelMap.addAttribute("rawResponseInfo", oAuthResponseBodyToString(oauthResponse));
    modelMap.addAttribute("accessToken", accessToken);
    setupModelMap(settings, "step3", request, modelMap, service);
    return "oauth-client";
  }

  @ModelAttribute("versions")
  public Collection<String> populateOAuthVersions() {
    return Arrays.asList(new String[] { OAuthVersion.VERSION10A.getVersion(), OAuthVersion.VERSION20.getVersion() });
  }

  @ModelAttribute("requestTokenVerbs")
  public Collection<String> populateRequestTokenVerbs() {
    return Arrays.asList(new String[] { Verb.POST.toString(), Verb.GET.toString() });
  }

  @ModelAttribute("accessTokenRequestOptions")
  public Collection<String> populateAccessTokenRequestOptions() {
    return Arrays.asList(new String[] { AccessTokenRequestOption.AUTHENTICATION_HEADER.getOption(),
        AccessTokenRequestOption.ENTITY_BODY_PARAMETERS.getOption(), AccessTokenRequestOption.QUERY_STRING_PARAMETERS.getOption() });
  }

  private void setupModelMap(ApiSettings settings, String step, HttpServletRequest request, ModelMap modelMap, OAuthService service) {
    settings.setStep(step);
    modelMap.addAttribute("settings", settings);
    request.getSession().setAttribute("settings", settings);
    request.getSession().setAttribute("service", service);
  }

  private ConfigurableOAuth10aServiceImpl getService10(ApiSettings settings) {
    ServiceBuilder builder = new ServiceBuilder()
        .provider(
            new ConfigurableApi10a(settings.getRequestTokenEndPoint(), settings.getAuthorizationURL(), settings.getAccessTokenEndPoint()))
        .apiKey(settings.getOauthKey()).apiSecret(settings.getOauthSecret()).callback(oauthCallbackUrl).debug();
    if (StringUtils.hasText(settings.getScope())) {
      builder.scope(settings.getScope());
    }
    ConfigurableOAuth10aServiceImpl service = (ConfigurableOAuth10aServiceImpl) builder.build();
    return service;
  }

  private ConfigurableOAuth20ServiceImpl getService20(ApiSettings settings) {
    ConfigurableApi20 api20 = new ConfigurableApi20(settings.getAccessTokenEndPoint2(), settings.getAuthorizationURL2(),
        settings.isImplicitGrant());
    ServiceBuilder provider = new ServiceBuilder().provider(api20);
    if (settings.isImplicitGrant()) {
      provider.apiSecret("DUMMY");
    } else {
      provider.apiSecret(settings.getOauthSecret());
    }
    ServiceBuilder builder = provider.apiKey(settings.getOauthKey()).debug();
    if (StringUtils.hasText(settings.getScope())) {
      builder.scope(settings.getScope());
    }
    if (!settings.isLeaveOutRedirectUri()) {
      builder.callback(oauthCallbackUrl);
    }
    ConfigurableOAuth20ServiceImpl service = (ConfigurableOAuth20ServiceImpl) builder.build();
    return service;
  }

}
