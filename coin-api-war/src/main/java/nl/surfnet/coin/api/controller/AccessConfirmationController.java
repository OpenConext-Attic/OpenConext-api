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

package nl.surfnet.coin.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.shared.log.diagnostics.ConextMDC;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Controller for retrieving the model for and displaying the confirmation page for access to a protected resource.
 */
@Controller
@SessionAttributes(types = AuthorizationRequest.class)
public class AccessConfirmationController {

  private ClientDetailsService clientDetailsService;

  @Value("${staticContentBasePath}")
  private String staticContentBasePath;

  @RequestMapping("/oauth2/confirm_access")
  public ModelAndView getAccessConfirmation(HttpServletRequest request,
                                            @ModelAttribute AuthorizationRequest clientAuth) throws Exception {
    ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
    TreeMap<String, Object> model = new TreeMap<String, Object>();
    model.put("auth_request", clientAuth);
    model.put("client", client);
    model.put("locale", RequestContextUtils.getLocale(request).toString());
    model.put("staticContentBasePath", staticContentBasePath);
    Map<String, String> languageLinks = new HashMap<String, String>();
    languageLinks.put("en", getUrlWithLanguageParam(request, "en"));
    languageLinks.put("nl", getUrlWithLanguageParam(request, "nl"));
    model.put("languageLinks", languageLinks);
    return new ModelAndView("access_confirmation", model);
  }

  @RequestMapping("/oauth/error")
  public ModelAndView handleError(HttpServletRequest request) {
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("error", request.getAttribute("error"));
    model.put("message", "There was a problem with the OAuth2 protocol");
    ConextMDC.markForWarn();
    return new ModelAndView("oauth_error", model);
  }


  @Autowired
  public void setClientDetailsService(ClientDetailsService clientDetailsService) {
    this.clientDetailsService = clientDetailsService;
  }

  /**
   * get a new URL based on the given request, with a lang= parameter set to the given language
   *
   * @param request HttpServletRequest
   * @param lang    the language
   * @return String
   */
  public static String getUrlWithLanguageParam(HttpServletRequest request, String lang) {
    String querystring;
    if (StringUtils.isBlank(request.getQueryString())) {
      querystring = "lang=" + lang;
    } else {

      String q = request.getQueryString();
      List<String> newParams = new ArrayList<String>();
      String[] params = q.split("&");
      for (String param : params) {
        String[] keyvalue = param.split("=");
        if ( ! StringUtils.equals(keyvalue[0], "lang")) {
          newParams.add(keyvalue[0] + "=" + keyvalue[1]);
        }
      }
      newParams.add("lang="+lang);
      querystring = StringUtils.join(newParams, "&");
    }

    return "?" + querystring;
  }
}
