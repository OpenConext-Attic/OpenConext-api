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
import java.util.Arrays;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.surfnet.coin.api.oauth.ClientMetaDataPrincipal;

import org.opensaml.saml2.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Handles the conversion of the SAML response and constructing a Principal
 * 
 */
public class SAMLAssertionAuthenticationFilter extends GenericFilterBean {

  private static final Logger LOG = LoggerFactory.getLogger(SAMLAuthenticationEntryPoint.class);

  private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

  @Resource(name = "openSAMLContext")
  private OpenSAMLContext openSAMLContext;


  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;

    if (!openSAMLContext.isSAMLResponse(req)) {
      LOG.debug("Request is not a SAML response. Will continue filter chain.");
      chain.doFilter(request, response);
      return;
    }
    SAMLAuthenticationToken samlAuthenticationToken = new SAMLAuthenticationToken(
      getPreAuthenticatedPrincipal(req),
      Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));

    samlAuthenticationToken.setAuthenticated(true);
    samlAuthenticationToken.setDetails(authenticationDetailsSource.buildDetails(req));
    SecurityContextHolder.getContext().setAuthentication(samlAuthenticationToken);

    String originalUrl = request.getParameter("RelayState");
    try {
      LOG.debug("Redirecting to original url {}", originalUrl);
      ((HttpServletResponse) response).sendRedirect(originalUrl);
    } catch (IOException e) {
      LOG.info("Cannot redirect to original url", e);
    }
  }

  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    if (!openSAMLContext.isSAMLResponse(request)) {
      return null;
    }
    Response samlResponse = openSAMLContext.extractSamlResponse(request);
    final UserDetails ud = openSAMLContext.authenticate(samlResponse);
    Assert.notNull(ud, "Authentication using a saml response should always yield a UserDetails.");
    return new ClientMetaDataPrincipal(ud.getUsername());
  }

}
