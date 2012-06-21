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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.StringUtils;

import nl.surfnet.coin.api.oauth.ClientMetaData;
import nl.surfnet.coin.api.oauth.ClientMetaDataHolder;
import nl.surfnet.coin.api.oauth.JanusClientMetadata;
import nl.surfnet.coin.api.oauth.OpenConextClientDetails;
import nl.surfnet.coin.api.oauth.OpenConextConsumerDetails;
import nl.surfnet.coin.janus.Janus;
import nl.surfnet.coin.janus.domain.ARP;
import nl.surfnet.coin.janus.domain.EntityMetadata;

/**
 * Client details service that uses Janus as backend. Implements both the oauth1
 * and oauth2 interface.
 */
public class JanusClientDetailsService implements OpenConextClientDetailsService {

  private final static Logger LOG = LoggerFactory.getLogger(JanusClientDetailsService.class);
  
  @Autowired
  private Janus janus;

  @Override
  @Cacheable(value = { "janus-meta-data" })
  public ClientDetails loadClientByClientId(String consumerKey) throws OAuth2Exception {
    EntityMetadata metadata = getJanusMetadataByConsumerKey(consumerKey);
    vaildateMetaData(consumerKey, metadata);
    final OpenConextClientDetails clientDetails = new OpenConextClientDetails();
    ClientMetaData clientMetaData = new JanusClientMetadata(metadata);
    clientDetails.setClientMetaData(clientMetaData);
    clientDetails.setClientSecret(metadata.getOauthConsumerSecret());
    clientDetails.setClientId(metadata.getOauthConsumerKey());
    clientDetails.setRegisteredRedirectUri(getCallbackUrlCollection(metadata));
    clientDetails.setScope(Arrays.asList("read"));
    ClientMetaDataHolder.setClientMetaData(clientMetaData);
    return clientDetails;
  }

  private void vaildateMetaData(String consumerKey, EntityMetadata metadata) {
    if (metadata == null) {
      String format = String.format("No unique consumer found by consumer key '%s'.",consumerKey);
      throw new RuntimeException(format);
    }
  }

  @Cacheable(value = { "janus-meta-data" })
  public ARP getArp(String clientId) {
    return janus.getArp(clientId);
  }

  /*
   * In janus we can set the callback url as a comma separated list which we need to convert
   */
  private Set<String> getCallbackUrlCollection(final EntityMetadata metadata) {
    final String callbackUrl = metadata.getOauthCallbackUrl();
    //sensible default
    Set<String> result = Collections.emptySet();
    if (callbackUrl != null) {
      if (callbackUrl.contains(",")) {
        //need to trim, therefore more code then calling StringUtils#commaDelimitedListToSet
        String[] callbacksArray = StringUtils.commaDelimitedListToStringArray(callbackUrl);
        result = new HashSet<String>();
        for (String callback : callbacksArray) {
          result.add(callback.trim());
        }
      } else {
        result = Collections.singleton(callbackUrl.trim());
      }
    }
    return result;
  }

  private EntityMetadata getJanusMetadataByConsumerKey(String consumerKey) {
    List<String> entityIds = janus.getEntityIdsByMetaData(Janus.Metadata.OAUTH_CONSUMERKEY, consumerKey);
    if (entityIds.size() != 1) {
      String format = String.format("Not a unique consumer (but %s) found by consumer key '%s'.",entityIds.size(),
          consumerKey);
      throw new RuntimeException(format);
    }
    String entityId = entityIds.get(0);
    return janus.getMetadataByEntityId(entityId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Cacheable(value = { "janus-meta-data" })
  public ConsumerDetails loadConsumerByConsumerKey(String consumerKey) throws OAuthException {
    EntityMetadata metadata = getJanusMetadataByConsumerKey(consumerKey);
    vaildateMetaData(consumerKey, metadata);
    final OpenConextConsumerDetails consumerDetails = new OpenConextConsumerDetails();
    consumerDetails.setConsumerKey(consumerKey);
    consumerDetails.setAuthorities(Arrays.<GrantedAuthority> asList(new SimpleGrantedAuthority("ROLE_USER")));
    ClientMetaData clientMetaData = new JanusClientMetadata(metadata);
    consumerDetails.setClientMetaData(clientMetaData);
    ClientMetaDataHolder.setClientMetaData(clientMetaData);

    consumerDetails.setSignatureSecret(new SharedConsumerSecret(metadata.getOauthConsumerSecret()));

    // set to required by default
    consumerDetails.setRequiredToObtainAuthenticatedToken(true);
    if (metadata.isTwoLeggedOauthAllowed()) {
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
