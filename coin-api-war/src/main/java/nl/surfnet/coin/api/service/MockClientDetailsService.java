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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
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

/**
 * Mock details service. Replacement for JanusClientDetailsService.
 */
public class MockClientDetailsService implements ClientDetailsService, ConsumerDetailsService {

  private static final Logger LOG = LoggerFactory.getLogger(MockClientDetailsService.class);

  private String defaultSecret = "mysecret";

  @Override
  public ClientDetails loadClientByClientId(String clientId) throws OAuth2Exception {
    final BaseClientDetails details = new BaseClientDetails();
    details.setClientId(clientId);
    details.setScope(Arrays.asList("read"));
    details.setClientSecret(defaultSecret);
    LOG.debug("Got request loadClientByClientId({}), will return: {}", clientId, details);
    return details;
  }

  @Override
  public ConsumerDetails loadConsumerByConsumerKey(String consumerKey) throws OAuthException {
    final BaseConsumerDetails consumerDetails = new BaseConsumerDetails();
    SignatureSecret secret = new SharedConsumerSecret(defaultSecret);
    consumerDetails.setConsumerKey(consumerKey);

    // Can do 2 legged
    consumerDetails.setRequiredToObtainAuthenticatedToken(false);
    consumerDetails.setAuthorities(Arrays.<GrantedAuthority>asList(new SimpleGrantedAuthority("ROLE_USER")));
    consumerDetails.setSignatureSecret(new SharedConsumerSecret(defaultSecret));
    LOG.debug("Got request loadClientByClientId({}), will return: {}", consumerKey, consumerDetails);
    return consumerDetails;

  }

  public void setDefaultSecret(String defaultSecret) {
    this.defaultSecret = defaultSecret;
  }
}
