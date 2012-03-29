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

package nl.surfnet.coin.api.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.Assert;
import org.springframework.web.util.UriUtils;

public class ShibbolethAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
  
  private static final Logger LOG = LoggerFactory.getLogger(ShibbolethAuthenticationEntryPoint.class);


  public ShibbolethAuthenticationEntryPoint(String loginFormUrl) {
    super(loginFormUrl);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
  }

  @Override
  protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
    final String url = super.buildRedirectUrlToLoginPage(request, response, authException);
    try {
      String concat = "?";
      if (url.contains("?")) {
        concat = "&";
      }
      String originalUrl = request.getRequestURL().append("?").append(request.getQueryString()).toString();
      final String urlToRedirectTo = url + concat + "target=" + UriUtils.encodeQueryParam(originalUrl, "utf-8");
      LOG.debug("URL to redirect to: {}", urlToRedirectTo);
      return urlToRedirectTo;

    } catch (UnsupportedEncodingException e) {
      LOG.error("Cannot encode target parameter in Shibboleth-URL.", e);
      return null;
    }

  }

}
