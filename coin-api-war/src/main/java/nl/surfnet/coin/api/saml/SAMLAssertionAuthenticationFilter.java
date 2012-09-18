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

package nl.surfnet.coin.api.saml;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.saml2.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.Assert;

import nl.surfnet.coin.api.oauth.ClientMetaDataPrincipal;

/**
 * Handles the conversion of the SAML response and constructing a Principal
 * 
 */
public class SAMLAssertionAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

  private static final Logger LOG = LoggerFactory.getLogger(SAMLAuthenticationEntryPoint.class);

  @Resource(name = "openSAMLContext")
  private OpenSAMLContext openSAMLContext;

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    if (!openSAMLContext.isSAMLResponse(request)) {
      return null;
    }
    Response samlResponse = openSAMLContext.extractSamlResponse(request);
    final UserDetails ud = openSAMLContext.authenticate(samlResponse);
    Assert.notNull(ud, "Authentication using a saml response should always yield a UserDetails.");
    return new ClientMetaDataPrincipal(ud.getUsername());
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
    super.successfulAuthentication(request, response, authResult);

    String originalUrl = request.getRequestURI()  + "?" + request.getParameter("RelayState");
    try {
      LOG.debug("Redirecting to original url {}", originalUrl);
      response.sendRedirect(originalUrl);
    } catch (IOException e) {
      LOG.info("Cannot redirect to original url", e);
    }
  }
}
