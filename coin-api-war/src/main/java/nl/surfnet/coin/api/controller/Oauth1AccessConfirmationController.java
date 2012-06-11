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

import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.surfnet.coin.api.oauth.ClientMetaData;
import nl.surfnet.coin.api.oauth.ExtendedBaseConsumerDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.security.oauth.provider.filter.UserAuthorizationProcessingFilter;
import org.springframework.security.oauth.provider.token.OAuthProviderToken;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for retrieving the model for and displaying the confirmation page
 * for access to a protected resource.
 * 
 * @author Ryan Heaton
 * @author Dave Syer
 */
@Controller
public class Oauth1AccessConfirmationController {

  @Autowired
  private OAuthProviderTokenServices tokenServices;

  @Autowired
  private ConsumerDetailsService clientDetailsService;

  @Value("${staticContentBasePath}")
  private String staticContentBasePath;

  @Resource(name = "oauthAuthenticateTokenFilter")
  private UserAuthorizationProcessingFilter userAuthorizationProcessingFilter;

  @RequestMapping("/oauth1/confirm_access")
  public ModelAndView getAccessConfirmation(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String token = request.getParameter("oauth_token");
    if (token == null) {
      throw new IllegalArgumentException("A request token to authorize must be provided.");
    }

    OAuthProviderToken providerToken = tokenServices.getToken(token);
    ConsumerDetails client = clientDetailsService.loadConsumerByConsumerKey(providerToken.getConsumerKey());

    String callback = request.getParameter("oauth_callback");
    TreeMap<String, Object> model = new TreeMap<String, Object>();
    model.put("oauth_token", token);
    if (callback != null) {
      model.put("oauth_callback", callback);
    }
    model.put("client", client);
    model.put("staticContentBasePath", staticContentBasePath);

    if (client instanceof ExtendedBaseConsumerDetails) {
      ClientMetaData clientMetaData = ((ExtendedBaseConsumerDetails) client).getClientMetaData();
      if (!clientMetaData.isConsentRequired()) {
        /*
         * We skip the consent screen, but to ensure that we hit all the filters and keep
         * the user flow intact we have implemented this using a javascript POST
         */
        return new ModelAndView("access_confirmation_oauth1_skip_consent", model);
      }
    }

    return new ModelAndView("access_confirmation_oauth1", model);
  }

  public void setTokenServices(OAuthProviderTokenServices tokenServices) {
    this.tokenServices = tokenServices;
  }

  public void setClientDetailsService(ConsumerDetailsService clientDetailsService) {
    this.clientDetailsService = clientDetailsService;
  }

  /**
   * @param userAuthorizationProcessingFilter
   *          the userAuthorizationProcessingFilter to set
   */
  public void setUserAuthorizationProcessingFilter(UserAuthorizationProcessingFilter userAuthorizationProcessingFilter) {
    this.userAuthorizationProcessingFilter = userAuthorizationProcessingFilter;
  }
}
