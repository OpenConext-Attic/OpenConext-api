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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.provider.BaseConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetails;

import nl.surfnet.coin.janus.Janus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class JanusClientDetailsServiceTest {

  @Mock
  private Janus janus;

  @InjectMocks
  private JanusClientDetailsService s;

  @Before
  public void init() {
    s = new JanusClientDetailsService();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void loadConsumerByConsumerKeyNotFound() {
    when(janus.getEntityIdsByMetaData(Janus.Metadata.OAUTH_CONSUMERKEY, "consumerkey"))
        .thenReturn(new ArrayList<String>());
    ConsumerDetails result = s.loadConsumerByConsumerKey("consumerkey");
    assertNull("service should return null in case consumer key not found.", result);
  }

  @Test
  public void loadConsumerByConsumerKeyNoSecretFound() {
    when(janus.getEntityIdsByMetaData(Janus.Metadata.OAUTH_CONSUMERKEY, "consumerkey2"))
        .thenReturn(new ArrayList<String>(Arrays.asList("identityId2")));
    when(janus.getMetadataByEntityId(eq("identityId2"), (Janus.Metadata[]) anyVararg())).thenReturn(null);
    ConsumerDetails result = s.loadConsumerByConsumerKey("consumerkey2");
    assertNull("service should return null when no  metadata found for consumer.",
        result);
  }

  @Test
  public void loadConsumerByConsumerKeyHappy() {
    when(janus.getEntityIdsByMetaData(Janus.Metadata.OAUTH_CONSUMERKEY, "consumerkey3"))
        .thenReturn(new ArrayList<String>(Arrays.asList("identityId3")));

    Map<String, String> metadata = new HashMap();
    metadata.put(Janus.Metadata.OAUTH_SECRET.val(), "secret");
    metadata.put(Janus.Metadata.OAUTH_TWOLEGGEDALLOWED.val(), "false");
    when(janus.getMetadataByEntityId(eq("identityId3"), (Janus.Metadata[]) anyVararg())).thenReturn(metadata);

    BaseConsumerDetails result = (BaseConsumerDetails) s.loadConsumerByConsumerKey("consumerkey3");

    assertEquals("service should return correct key", "consumerkey3", result.getConsumerKey());
    assertEquals("service should return correct secret", "secret",
        ((SharedConsumerSecret) result.getSignatureSecret()).getConsumerSecret());
    assertEquals("service should return whether client is required to authenticate (so two legged NOT allowed)",
        true, result.isRequiredToObtainAuthenticatedToken());
  }
}
