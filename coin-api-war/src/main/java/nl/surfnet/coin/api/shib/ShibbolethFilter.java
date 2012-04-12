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

package nl.surfnet.coin.api.shib;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.EnumerationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class ShibbolethFilter extends AbstractAuthenticationProcessingFilter {

  private static final Logger LOG = LoggerFactory.getLogger(ShibbolethFilter.class);

  /**
   * @param defaultFilterProcessesUrl the default value for <tt>filterProcessesUrl</tt>.
   */
  protected ShibbolethFilter(String defaultFilterProcessesUrl) {
    super(defaultFilterProcessesUrl);
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    LOG.debug("Hitting ShibbolethFilter");
    super.doFilter(req, res, chain);
  }

  @Override
  protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
    LOG.debug("Hitting ShibbolethFilters requiresAuth (returns {}, {})", super.requiresAuthentication(request, response), request.getHeader("REMOTE_USER") != null);
    LOG.debug("cp: {}, processuri: {}, request-uri: {}", new Object[] {request.getContextPath(), getFilterProcessesUrl(), request.getRequestURI()});
    LOG.debug("remote-user: '{}'", request.getHeader("REMOTE_USER"));


    return (request.getHeader("REMOTE_USER") != null);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
    LOG.debug("Request headers: {}", EnumerationUtils.toList(request.getHeaderNames()));
    if (request.getHeader("REMOTE_USER") != null) {
      ShibbolethAuthenticationToken token = new ShibbolethAuthenticationToken(null);
      token.setDetails(request.getHeader("REMOTE_USER"));
      LOG.debug("Attempting authentication, REMOTE_USER-header: {}", request.getHeader("REMOTE_USER"));
      return this.getAuthenticationManager().authenticate(token);
    } else {
      LOG.debug("No REMOTE_USER header set");
      return null;
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
    SecurityContextHolder.getContext().setAuthentication(authResult);
    LOG.debug("successfulAuthentication: Letting chain continue.");

    chain.doFilter(request, response);
  }
}
