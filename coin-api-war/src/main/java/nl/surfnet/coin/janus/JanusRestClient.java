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
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class JanusRestClient implements Janus {

  Logger LOG = LoggerFactory.getLogger(JanusRestClient.class);

  @Autowired
  RestTemplate restTemplate;

  @Value("${janus.uri}")
  private URI janusUri;

  @Value("${janus.user}")
  private String user;

  @Value("${janus.secret}")
  private String secret;

  @Override
  public String getOauthSecretByClientId(String clientId) {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("spentityid", clientId);
    parameters.put("keys", ""); // TODO: filter strictly

    URI signedUri = null;
    try {
      signedUri = sign("getSpList", parameters);
      return restTemplate.getForObject(signedUri, String.class);

    } catch (NoSuchAlgorithmException e) {
      LOG.error("While doing Janus-request", e);
    } catch (IOException e) {
      LOG.error("While doing Janus-request", e);
    }
    return null;
  }

  public URI sign(String method, Map<String, String> parameters) throws NoSuchAlgorithmException, IOException {
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
    //   System.out.println(toSign);
    //do8He2KKd6m1   janus_key   engineblock   keys   method   getSpList   rest  1   userid   engineblock
    MessageDigest digest = MessageDigest.getInstance("SHA-512");
    digest.reset();
    byte[] input = digest.digest(toSign.toString().getBytes("UTF-8"));
    char[] value = Hex.encodeHex(input);
    String janus_sig = new String(value);
    keys.put("janus_sig", janus_sig);
    // System.out.println(janus_sig);
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
}
