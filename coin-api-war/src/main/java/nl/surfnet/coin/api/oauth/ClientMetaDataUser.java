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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.sun.media.jai.codec.SeekableOutputStream;

import nl.surfnet.coin.janus.Janus;
import nl.surfnet.coin.janus.Janus.Metadata;

/**
 * Encapsulated the {@link Metadata} from {@link Janus}
 *
 */
@SuppressWarnings("serial")
public class ClientMetaDataUser extends User{
  
  private ClientMetaData clientMetaData;

  /**
   * See {@link User#User(String, String, boolean, boolean, boolean, boolean, Collection)}
   */
  public ClientMetaDataUser(String username, String password, boolean enabled, boolean accountNonExpired,
      boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, ClientMetaData clientMetaData) {
    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    this.clientMetaData = clientMetaData;
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
