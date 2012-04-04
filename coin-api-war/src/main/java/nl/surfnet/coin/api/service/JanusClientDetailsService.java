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
import java.util.Map;

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
    clientDetails.setClientSecret(janus.getMetadataByClientId(clientId).get(Janus.Metadata.OAUTH_CONSUMERSECRET.val()));
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
    final Map<String, String> metadata = janus.getMetadataByClientId(consumerKey);
    consumerDetails.setConsumerKey(consumerKey);

    SignatureSecret secret;
    /*
      Here we always use the Janus.Metadata.OAUTH_SECRET as the client's secret in case the consumer has two legged access.
      However, if a two-legged allowed client performs a 3 legged request, this will fail (as he will sign with his Janus.Metadata.OAUTH_SECRET secret)
      FIXME: set secret based on type of issued request
     */
    if (metadata.get(Janus.Metadata.OAUTH_SECRET.val()) != null) {
      // consumer has a 'secret' attribute; therefore allowed to do two legged OAuth 1.0
      consumerDetails.setRequiredToObtainAuthenticatedToken(false);
      consumerDetails.setAuthorities(Arrays.<GrantedAuthority>asList(new SimpleGrantedAuthority("ROLE_USER")));
      secret = new SharedConsumerSecret(metadata.get(Janus.Metadata.OAUTH_SECRET.val()));
    } else {
      consumerDetails.setRequiredToObtainAuthenticatedToken(true);
      secret = new SharedConsumerSecret(metadata.get(Janus.Metadata.OAUTH_CONSUMERSECRET.val()));
    }
    consumerDetails.setSignatureSecret(secret);
    return consumerDetails;
  }
}
