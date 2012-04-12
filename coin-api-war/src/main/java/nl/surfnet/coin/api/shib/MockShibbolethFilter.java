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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.collections.EnumerationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MockShibbolethFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(MockShibbolethFilter.class);

  private String remoteUser;
  
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.remoteUser = filterConfig.getInitParameter("remoteUser");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    LOG.debug("hitting MockShibbolethFilter");
    if ("true".equals(request.getParameter("shibbed"))) {
      LOG.debug("parameter shibbed = true, will wrap request.");
      // replace request with a version that mocks the REMOTE_USER header
      request = new HttpServletRequestWrapper((HttpServletRequest) request) {
        @Override
        public String getHeader(String name) {
          if (name.equals("REMOTE_USER")) {
            return remoteUser;
          } else if (name.equals("displayName")) {
            return String.format("%s's Display Name", remoteUser);
          } else if (name.equals("schacHomeOrganization")) {
            return String.format("%s's Schac Home Organization", remoteUser);
          } else {
            return super.getHeader(name);
          }
        }

        @Override
        public Enumeration getHeaders(String name) {
          if (name.equals("REMOTE_USER")) {
            throw new UnsupportedOperationException("Mock not implemented for multiple REMOTE_USER headers.");
          }
          return super.getHeaders(name);
        }
      };
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
  }
}
