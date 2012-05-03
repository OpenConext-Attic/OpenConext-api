/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nl.surfnet.coin.api.oauth;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;

/**
 * {@link PreAuthenticatedGrantedAuthoritiesUserDetailsService} that create
 * UserDetails that compass the details of the Janus metadata
 * 
 */
public class ClientMetaDataPreAuthenticatedGrantedAuthoritiesUserDetailsService extends
    PreAuthenticatedGrantedAuthoritiesUserDetailsService {

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.security.web.authentication.preauth.
   * PreAuthenticatedGrantedAuthoritiesUserDetailsService
   * #createuserDetails(org.springframework.security.core.Authentication,
   * java.util.Collection)
   */
  @Override
  protected UserDetails createuserDetails(Authentication token, Collection<? extends GrantedAuthority> authorities) {
    if (token instanceof PreAuthenticatedAuthenticationToken) {
      PreAuthenticatedAuthenticationToken preToken = (PreAuthenticatedAuthenticationToken) token;
      Object principal = preToken.getPrincipal();
      if (principal instanceof ClientMetaDataPrincipal) {
        return new ClientMetaDataUser(token.getName(), "N/A", true, true, true, true, authorities,
            ((ClientMetaDataPrincipal) principal).getClientMetaData());
      } else {
        throw new RuntimeException("The principal on the PreAuthenticatedAuthenticationToken is of the type '"
            + (principal != null ? principal.getClass() : "null")
            + "'. Required is a (sub)class of ClientMetaDataPrincipal");
      }

    } else {
      throw new RuntimeException("The token is of the type '" + (token != null ? token.getClass() : "null")
          + "'. Required is a (sub)class of PreAuthenticatedAuthenticationToken");
    }

  }

}
