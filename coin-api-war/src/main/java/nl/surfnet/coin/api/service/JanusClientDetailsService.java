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

package nl.surfnet.coin.api.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.common.signature.SignatureSecret;
import org.springframework.security.oauth.provider.BaseConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;

import nl.surfnet.coin.janus.Janus;

/**
 * Client details service that uses Janus as backend. Implements both the oauth1 and oauth2 interface.
 */
public class JanusClientDetailsService implements ClientDetailsService, ConsumerDetailsService {

  @Autowired
  private Janus janus;

  /**
   * {@InheritDoc}
   */
  @Override
  public ClientDetails loadClientByClientId(String clientId) throws OAuth2Exception {
    final BaseClientDetails clientDetails = new BaseClientDetails();
    clientDetails.setClientSecret(janus.getOauthSecretByClientId(clientId));
    clientDetails.setClientId(clientId);
    clientDetails.setScope(Arrays.asList("read"));
    return clientDetails;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConsumerDetails loadConsumerByConsumerKey(String consumerKey) throws OAuthException {
    final BaseConsumerDetails consumerDetails = new BaseConsumerDetails();
    SignatureSecret secret = new SharedConsumerSecret(janus.getOauthSecretByClientId(consumerKey));
    consumerDetails.setSignatureSecret(secret);
    consumerDetails.setConsumerKey(consumerKey);
    consumerDetails.setRequiredToObtainAuthenticatedToken(false);
    return consumerDetails;
  }
}
