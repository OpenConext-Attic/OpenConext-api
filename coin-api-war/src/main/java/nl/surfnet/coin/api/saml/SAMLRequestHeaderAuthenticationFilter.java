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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.api.oauth.ClientMetaDataPrincipal;
import nl.surfnet.spring.security.opensaml.SAMLMessageHandler;
import nl.surfnet.spring.security.opensaml.ServiceProviderAuthenticationException;

import org.apache.commons.lang.StringUtils;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.core.Response;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

/**
 * Handles the conversion of the SAML response and constructing a Principal
 * 
 */
public class SAMLRequestHeaderAuthenticationFilter extends RequestHeaderAuthenticationFilter {

  private static final Logger LOG = LoggerFactory.getLogger(SAMLAuthenticationEntryPoint.class);

  @Resource(name = "openSAMLContext")
  private OpenSAMLContext openSAMLContext;

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    if (openSAMLContext.isSAMLResponse(request)) {
      Response samlResponse = openSAMLContext.extractSamlResponse(request);
      final UserDetails ud = openSAMLContext.authenticate(samlResponse);
      return ud == null ? null : ud.getUsername();
    } else {
      return null;
    }
  }

}
