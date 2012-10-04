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

package nl.surfnet.coin.janus;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import nl.surfnet.coin.janus.domain.ARP;
import nl.surfnet.coin.janus.domain.EntityMetadata;
import nl.surfnet.coin.janus.domain.JanusEntity;

/**
 * REST client implementation for Janus.
 */
public class JanusRestClient implements Janus {

  private static Logger LOG = LoggerFactory.getLogger(JanusRestClient.class);

  private RestTemplate restTemplate;

  private URI janusUri;

  private String user;

  private String secret;

  public JanusRestClient() {
    this.restTemplate = new RestTemplate();
    restTemplate
        .setMessageConverters(Arrays.<HttpMessageConverter<?>> asList(new MappingJacksonHttpMessageConverter()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public EntityMetadata getMetadataByEntityId(String entityId) {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("entityid", entityId);
    final Collection metadataAsStrings = CollectionUtils.collect(Arrays.asList(Metadata.values()), new Transformer() {
      @Override
      public Object transform(Object input) {
        return ((Metadata) input).val();
      }
    });

    parameters.put("keys", StringUtils.join(metadataAsStrings, ','));

    URI signedUri;
    try {
      signedUri = sign("getMetadata", parameters);

      if (LOG.isDebugEnabled()) {
        LOG.debug("Signed Janus-request is: {}", signedUri);
      }

      final Map<String, Object> restResponse = restTemplate.getForObject(signedUri, Map.class);
      Assert.notNull(restResponse, "Rest response from Janus should not be null");

      if (LOG.isDebugEnabled()) {
        LOG.debug("Janus-request returned: {}", restResponse.toString());
      }

      final EntityMetadata entityMetadata = EntityMetadata.fromMetadataMap(restResponse);

      entityMetadata.setAppEntityId(entityId);
      return entityMetadata;
    } catch (IOException e) {
      LOG.error("While doing Janus-request", e);
    }
    return null;
  }

  @Override
  public List<String> getEntityIdsByMetaData(Metadata key, String value) {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("key", key.val());
    parameters.put("value", value);

    URI signedUri;
    try {
      signedUri = sign("findIdentifiersByMetadata", parameters);

      if (LOG.isDebugEnabled()) {
        LOG.debug("Signed Janus-request is: {}", signedUri);
      }

      final List<String> restResponse = restTemplate.getForObject(signedUri, List.class);

      if (LOG.isDebugEnabled()) {
        LOG.debug("Janus-request returned: {}", restResponse.toString());
      }

      return restResponse;

    } catch (IOException e) {
      LOG.error("While doing Janus-request", e);
    }
    return null;

  }

  @Override
  public List<String> getAllowedSps(String idpentityid) {
    return getAllowedSps(idpentityid, null);
  }

  @Override
  public List<String> getAllowedSps(String idpentityid, String revision) {

    Assert.hasText(idpentityid, "idpentityid is a required parameter");
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("idpentityid", idpentityid);
    if (!StringUtils.isBlank(revision)) {
      parameters.put("idprevision", revision);
    }

    URI signedUri;
    try {
      signedUri = sign("getAllowedSps", parameters);

      if (LOG.isDebugEnabled()) {
        LOG.debug("Signed Janus-request is: {}", signedUri);
      }

      final List<String> restResponse = restTemplate.getForObject(signedUri, List.class);

      if (LOG.isDebugEnabled()) {
        LOG.debug("Janus-request returned: {}", restResponse.toString());
      }

      return restResponse;

    } catch (IOException e) {
      LOG.error("While doing Janus-request", e);
    }
    return null;
  }

  @Override
  public List<EntityMetadata> getSpList() {

    Map<String, String> parameters = new HashMap<String, String>();

    final Collection metadataAsStrings = CollectionUtils.collect(Arrays.asList(Metadata.values()), new Transformer() {
      @Override
      public Object transform(Object input) {
        return ((Metadata) input).val();
      }
    });

    parameters.put("keys", StringUtils.join(metadataAsStrings, ','));

    URI signedUri;
    try {
      signedUri = sign("getSpList", parameters);

      if (LOG.isDebugEnabled()) {
        LOG.debug("Signed Janus-request is: {}", signedUri);
      }

      final Map<String, Map<String, Object>> restResponse = restTemplate.getForObject(signedUri, Map.class);

      if (LOG.isDebugEnabled()) {
        LOG.debug("Janus-request returned: {}", restResponse.toString());
      }
      List<EntityMetadata> entities = new ArrayList<EntityMetadata>();
      for (Map.Entry<String, Map<String, Object>> entry : restResponse.entrySet()) {
        String entityId = entry.getKey();
        final EntityMetadata e = EntityMetadata.fromMetadataMap(entry.getValue());
        e.setAppEntityId(entityId);
        entities.add(e);
      }

      return entities;

    } catch (IOException e) {
      LOG.error("While doing Janus-request", e);
    }
    return null;
  }

  @Override
  public List<EntityMetadata> getIdpList() {

    Map<String, String> parameters = new HashMap<String, String>();

    final Collection metadataAsStrings = CollectionUtils.collect(Arrays.asList(Metadata.values()), new Transformer() {
      @Override
      public Object transform(Object input) {
        return ((Metadata) input).val();
      }
    });

    parameters.put("keys", StringUtils.join(metadataAsStrings, ','));

    URI signedUri;
    try {
      signedUri = sign("getIdpList", parameters);

      if (LOG.isDebugEnabled()) {
        LOG.debug("Signed Janus-request is: {}", signedUri);
      }

      final Map<String, Map<String, Object>> restResponse = restTemplate.getForObject(signedUri, Map.class);

      if (LOG.isDebugEnabled()) {
        LOG.debug("Janus-request returned: {}", restResponse.toString());
      }
      List<EntityMetadata> entities = new ArrayList<EntityMetadata>();
      for (Map.Entry<String, Map<String, Object>> entry : restResponse.entrySet()) {
        String entityId = entry.getKey();
        final EntityMetadata e = EntityMetadata.fromMetadataMap(entry.getValue());
        e.setAppEntityId(entityId);
        entities.add(e);
      }

      return entities;

    } catch (IOException e) {
      LOG.error("While doing Janus-request", e);
    }
    return null;
  }

  @Override
  public ARP getArp(String entityId) {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("entityid", entityId);
    URI signedUri = null;
    try {
      signedUri = sign("arp", parameters);
      LOG.debug("Signed Janus-request is: {}", signedUri);
    } catch (IOException e) {
      LOG.error("Could not do ARP request to Janus", e);
    }

    final Map restResponse = restTemplate.getForObject(signedUri, Map.class);
    LOG.debug("Janus-request returned {}", restResponse);

    return (restResponse == null || restResponse.isEmpty()) ? null : ARP.fromRestResponse(restResponse);
  }

  @Override
  public boolean isConnectionAllowed(String spEntityId, String idpEntityId) {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("spentityid", spEntityId);
    parameters.put("idpentityid", idpEntityId);
    URI signedUri = null;
    try {
      signedUri = sign("isConnectionAllowed", parameters);
      LOG.debug("Signed Janus-request is: {}", signedUri);
    } catch (IOException e) {
      LOG.error("Could not do isConnectionAllowed request to Janus", e);
    }

    final List restResponse = restTemplate.getForObject(signedUri, List.class);
    LOG.debug("Janus-request returned {}", restResponse);

    return CollectionUtils.isEmpty(restResponse) ? false : (Boolean) restResponse.get(0);
  }

  @Override
  public JanusEntity getEntity(String entityId) {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("entityid", entityId);
    URI signedUri = null;
    try {
      signedUri = sign("getEntity", parameters);
      LOG.debug("Signed Janus-request is: {}", signedUri);
    } catch (IOException e) {
      LOG.error("Could not do getEntity request to Janus", e);
    }

    @SuppressWarnings("unchecked") final Map<String, Object> restResponse = restTemplate.getForObject(signedUri, Map.class);
    LOG.debug("Janus-request returned {}", restResponse);

    return restResponse == null ? null : JanusEntity.fromJanusResponse(restResponse);
  }

  /**
   * Sign the given method call.
   * 
   * @param method
   *          the name of the method to call
   * @param parameters
   *          additional parameters that need to be passed to Janus
   * @return URI with parameters janus_sig and janus_key
   * @throws IOException
   */
  private URI sign(String method, Map<String, String> parameters) throws IOException {
    Map<String, String> keys = new TreeMap<String, String>();
    keys.put("janus_key", user);
    keys.put("method", method);

    keys.putAll(parameters);

    keys.put("rest", "1");
    keys.put("userid", user);
    Set<String> keySet = keys.keySet();
    StringBuilder toSign = new StringBuilder(secret);
    for (String key : keySet) {
      toSign.append(key);
      toSign.append(keys.get(key));
    }

    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("SHA-512");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Cannot use algorithm SHA-512", e);
    }
    digest.reset();
    final String charsetName = "UTF-8";
    byte[] input = digest.digest(toSign.toString().getBytes(charsetName));
    char[] value = Hex.encodeHex(input);
    String janus_sig = new String(value);
    keys.put("janus_sig", janus_sig);

    StringBuilder url = new StringBuilder();
    keySet = keys.keySet();
    for (String key : keySet) {
      if (url.length() > 0) {
        url.append('&');
      }
      url.append(key).append('=').append(URLEncoder.encode(keys.get(key), charsetName));
    }
    String uri = url.toString();
    return URI.create(janusUri + "?" + uri);
  }

  /**
   * @param restTemplate
   *          the restTemplate to set
   */
  public void setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * @param janusUri
   *          the janusUri to set
   */
  public void setJanusUri(URI janusUri) {
    this.janusUri = janusUri;
  }

  /**
   * @param user
   *          the user to set
   */
  public void setUser(String user) {
    this.user = user;
  }

  /**
   * @param secret
   *          the secret to set
   */
  public void setSecret(String secret) {
    this.secret = secret;
  }
}
