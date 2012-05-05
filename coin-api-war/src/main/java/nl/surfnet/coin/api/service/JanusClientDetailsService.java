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
import org.springframework.cache.annotation.Cacheable;
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

import nl.surfnet.coin.api.oauth.ClientMetaData;
import nl.surfnet.coin.api.oauth.ClientMetaDataHolder;
import nl.surfnet.coin.api.oauth.ExtendedBaseClientDetails;
import nl.surfnet.coin.api.oauth.ExtendedBaseConsumerDetails;
import nl.surfnet.coin.janus.Janus;
import nl.surfnet.coin.janus.Janus.Metadata;

/**
 * Client details service that uses Janus as backend. Implements both the oauth1
 * and oauth2 interface.
 */
public class JanusClientDetailsService implements ClientDetailsService, ConsumerDetailsService {

  private final static Logger LOG = LoggerFactory.getLogger(JanusClientDetailsService.class);
  
  @Autowired
  private Janus janus;

  /*
   * All the meta data fields we need from Janus 
   */
  public static final Metadata[] METADATA_TO_FETCH = { Metadata.OAUTH_SECRET, Metadata.OAUTH_CONSUMERKEY,
      Metadata.OAUTH_CALLBACKURL, Metadata.OAUTH_TWOLEGGEDALLOWED, Metadata.OAUTH_APPTITLE,
      Metadata.OAUTH_APPDESCRIPTION, Metadata.OAUTH_APPTHUMBNAIL, Metadata.OAUTH_APPICON };

  @Override
  @Cacheable(value = { "janus-meta-data" })
  public ClientDetails loadClientByClientId(String consumerKey) throws OAuth2Exception {
    Map<String, String> metadata = getJanusMetadataByConsumerKey(consumerKey);
    if (metadata == null) {
      return null;
    } 
      final ExtendedBaseClientDetails clientDetails = new ExtendedBaseClientDetails();
      ClientMetaData clientMetaData = ClientMetaData.fromMetaData(metadata);
      clientDetails.setClientMetaData(clientMetaData);
      clientDetails.setClientSecret(metadata.get(Janus.Metadata.OAUTH_SECRET.val()));
      clientDetails.setClientId(metadata.get(Janus.Metadata.OAUTH_CONSUMERKEY.val()));
      clientDetails.setScope(Arrays.asList("read"));
      ClientMetaDataHolder.setClientMetaData(clientMetaData);
      return clientDetails;
    
  }

  private Map<String, String> getJanusMetadataByConsumerKey(String consumerKey) {
    List<String> entityIds = janus.getEntityIdsByMetaData(Janus.Metadata.OAUTH_CONSUMERKEY, consumerKey);
    if (entityIds.size() != 1) {
      LOG.info("Not a unique consumer (but {}) found by consumer key '{}'. Will return null.", entityIds.size(),
          consumerKey);
      return null;
    }
    String entityId = entityIds.get(0);
    return janus.getMetadataByEntityId(entityId, METADATA_TO_FETCH);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Cacheable(value = { "janus-meta-data" })
  public ConsumerDetails loadConsumerByConsumerKey(String consumerKey) throws OAuthException {
    Map<String, String> metadata = getJanusMetadataByConsumerKey(consumerKey);

    if (metadata == null) {
      return null;
    }

    final ExtendedBaseConsumerDetails consumerDetails = new ExtendedBaseConsumerDetails();
    // even if additional metadata not found, set these properties.
    consumerDetails.setConsumerKey(consumerKey);
    consumerDetails.setAuthorities(Arrays.<GrantedAuthority> asList(new SimpleGrantedAuthority("ROLE_USER")));
    ClientMetaData clientMetaData = ClientMetaData.fromMetaData(metadata);
    consumerDetails.setClientMetaData(clientMetaData);
    ClientMetaDataHolder.setClientMetaData(clientMetaData);

    // set to required by default
    consumerDetails.setRequiredToObtainAuthenticatedToken(true);

    consumerDetails.setSignatureSecret(new SharedConsumerSecret(metadata.get(Janus.Metadata.OAUTH_SECRET.val())));
    if (StringUtils.equals("true", metadata.get(Janus.Metadata.OAUTH_TWOLEGGEDALLOWED.val()))) {
      // two legged allowed
      consumerDetails.setRequiredToObtainAuthenticatedToken(false);
    }

    return consumerDetails;
  }

  /**
   * @param janus the janus to set
   */
  public void setJanus(Janus janus) {
    this.janus = janus;
  }
}
