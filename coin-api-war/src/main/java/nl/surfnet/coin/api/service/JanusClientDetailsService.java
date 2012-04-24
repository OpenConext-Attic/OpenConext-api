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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
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

  private final static Logger LOG = LoggerFactory.getLogger(JanusClientDetailsService.class);
  @Autowired
  private Janus janus;


  @Override
  public ClientDetails loadClientByClientId(String consumerKey) throws OAuth2Exception {

    Map<String, String> metadata = getJanusMetadataByConsumerKey(consumerKey);

    final BaseClientDetails clientDetails = new BaseClientDetails();
    clientDetails.setClientSecret(metadata.get(Janus.Metadata.OAUTH_SECRET.name()));
    clientDetails.setClientId(metadata.get(Janus.Metadata.ENTITY_ID.name()));
    clientDetails.setScope(Arrays.asList("read"));
    return clientDetails;
  }


  private Map<String, String> getJanusMetadataByConsumerKey(String consumerKey) {
    List<String> entityIds = janus.getEntityIdsByMetaData(Janus.Metadata.OAUTH_CONSUMERKEY, consumerKey);
    if (entityIds.size() != 1) {
      LOG.info("Not a unique consumer (but {}) found by consumer key '{}'. Will return null.", entityIds.size(), consumerKey);
      return null;
    }
    String entityId = entityIds.get(0);

    return janus.getMetadataByEntityId(entityId,
        Janus.Metadata.OAUTH_TWOLEGGEDALLOWED,
        Janus.Metadata.OAUTH_CALLBACKURL,
        Janus.Metadata.OAUTH_SECRET);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConsumerDetails loadConsumerByConsumerKey(String consumerKey) throws OAuthException {

    final BaseConsumerDetails consumerDetails = new BaseConsumerDetails();
    // even if additional metadata not found, set these properties.
    consumerDetails.setConsumerKey(consumerKey);
    consumerDetails.setAuthorities(Arrays.<GrantedAuthority>asList(new SimpleGrantedAuthority("ROLE_USER")));


    // set to required by default
    consumerDetails.setRequiredToObtainAuthenticatedToken(true);

    Map<String, String> metadata = getJanusMetadataByConsumerKey(consumerKey);

    if (metadata == null) {
      return null;
    } else {
      consumerDetails.setSignatureSecret(new SharedConsumerSecret(metadata.get(Janus.Metadata.OAUTH_SECRET.val())));
      if (StringUtils.equals("true", metadata.get(Janus.Metadata.OAUTH_TWOLEGGEDALLOWED.val()))) {
        // two legged allowed
        consumerDetails.setRequiredToObtainAuthenticatedToken(false);
      }
    }
    return consumerDetails;
  }
}
