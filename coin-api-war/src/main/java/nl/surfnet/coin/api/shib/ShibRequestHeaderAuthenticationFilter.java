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

import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.api.oauth.ClientMetaDataPrincipal;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

/**
 * Specific RequestHeaderAuthenticationFilter that interprets an empty request header as one that is not set at all.
 *
 */
public class ShibRequestHeaderAuthenticationFilter extends RequestHeaderAuthenticationFilter {
  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {

    String remoteUser = request.getHeader("REMOTE_USER");
    if (StringUtils.isNotEmpty(remoteUser)) {
      return new ClientMetaDataPrincipal(remoteUser);
    } else {
      return null;
    }
  }
}
