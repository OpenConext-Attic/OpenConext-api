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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class ShibbolethAuthenticationManager implements AuthenticationManager {

  private static final Logger LOG = LoggerFactory.getLogger(ShibbolethAuthenticationManager.class);

  /**
   * Very simple implementation: the actual authentication is done by Shibboleth.
   *
   * @param authentication
   * @return
   * @throws AuthenticationException
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    final ShibbolethAuthenticationToken newAuthenticationToken = new ShibbolethAuthenticationToken(
        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    newAuthenticationToken.setAuthenticated(true);
    newAuthenticationToken.setDetails(authentication.getDetails());
    LOG.debug("Will return the authentication: {}", newAuthenticationToken);
    return newAuthenticationToken;
  }


}
