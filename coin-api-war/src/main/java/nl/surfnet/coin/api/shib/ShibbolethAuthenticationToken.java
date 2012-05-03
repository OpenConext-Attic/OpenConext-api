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

import java.util.Collection;

import nl.surfnet.coin.api.oauth.ClientMetaData;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@SuppressWarnings("serial")
public class ShibbolethAuthenticationToken extends AbstractAuthenticationToken {

  private ClientMetaData clientMetaData;
  
  public ShibbolethAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
  }

  @Override
  public Object getCredentials() {
    return "";
  }

  /**
   * Details only contains the username (the REMOTE_USER from shib)
   * @return
   */
  @Override
  public Object getPrincipal() {
    return getDetails();
  }

  /**
   * @return the clientMetaData
   */
  public ClientMetaData getClientMetaData() {
    return clientMetaData;
  }

  /**
   * @param clientMetaData the clientMetaData to set
   */
  public void setClientMetaData(ClientMetaData clientMetaData) {
    this.clientMetaData = clientMetaData;
  }
}
