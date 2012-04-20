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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

/**
 * REST client implementation for Janus.
 */
public class JanusRestClient implements Janus {

  private static  Logger LOG = LoggerFactory.getLogger(JanusRestClient.class);

  private static final String METADATA_TO_FETCH = "coin:oauth:consumer_secret,coin:oauth:secret";

  @Autowired
  private RestTemplate restTemplate;

  @Value("${janus.uri}")
  private URI janusUri;

  @Value("${janus.user}")
  private String user;

  @Value("${janus.secret}")
  private String secret;

  /**
   * {@inheritDoc}
   */
  @Override
  public Map <String,String> getMetadataByEntityId(String entityId, Metadata... metadatas) {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("entityid", entityId);
    final Collection metadataAsStrings = CollectionUtils.collect(Arrays.asList(metadatas), new Transformer() {
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

      final Map<String, String>restResponse = restTemplate.getForObject(signedUri, Map.class);
      
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

  /**
   * Sign the given method call.
   * @param method the name of the method to call
   * @param parameters additional parameters that need to be passed to Janus
   * @return URI with parameters janus_sig and janus_key
   * @throws NoSuchAlgorithmException
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

    MessageDigest digest = null;
    try {
      digest = MessageDigest.getInstance("SHA-512");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    digest.reset();
    byte[] input = digest.digest(toSign.toString().getBytes("UTF-8"));
    char[] value = Hex.encodeHex(input);
    String janus_sig = new String(value);
    keys.put("janus_sig", janus_sig);

    StringBuilder url = new StringBuilder();
    keySet = keys.keySet();
    for (String key : keySet) {
      if (url.length() > 0) {
        url.append("&");
      }
      url.append(String.format("%s=%s", key, keys.get(key)));
    }
    return URI.create(janusUri + "?" + url.toString());
  }

  /**
   * @param restTemplate the restTemplate to set
   */
  public void setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * @param janusUri the janusUri to set
   */
  public void setJanusUri(URI janusUri) {
    this.janusUri = janusUri;
  }

  /**
   * @param user the user to set
   */
  public void setUser(String user) {
    this.user = user;
  }

  /**
   * @param secret the secret to set
   */
  public void setSecret(String secret) {
    this.secret = secret;
  }
}
