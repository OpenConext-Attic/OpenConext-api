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
package nl.surfnet.coin.api.basic;

import java.util.Arrays;
import java.util.Collection;

import nl.surfnet.coin.api.shib.ShibbolethAuthenticationToken;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * {@link AuthenticationManager} that is used in the basic external mock group
 * provider
 * 
 */
public class MockBasicAuthenticationManager implements AuthenticationManager {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.security.authentication.AuthenticationManager#authenticate
   * (org.springframework.security.core.Authentication)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
        authentication.getCredentials(), Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    token.setDetails(authentication.getDetails());
    return token;
  }

}
