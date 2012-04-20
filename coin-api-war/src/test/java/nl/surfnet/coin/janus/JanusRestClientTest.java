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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:JanusRestClientTest-context.xml")
public class JanusRestClientTest {

  @Autowired
  @InjectMocks
  private Janus janusRestClient;

  @Mock
  private RestTemplate restTemplate;

  @Before
  public void before() {
        MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getMetadataByEntityId() {
    ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
    final HashMap<String, String> map = new HashMap<String, String>();
    map.put("coin:oauth:secret", "hissecret");
    when(restTemplate.getForObject((URI) anyObject(), eq(Map.class))).thenReturn(map);
    String result = janusRestClient.getMetadataByEntityId("foo", Janus.Metadata.OAUTH_SECRET).get(Janus.Metadata.OAUTH_SECRET.val());
    assertEquals("hissecret", result);
    verify(restTemplate).getForObject(captor.capture(), eq(Map.class));
    assertTrue("Query string should contain correct entityid", captor.getValue().getQuery().contains("entityid=foo"));
    assertTrue("Query string should contain correct method", captor.getValue().getQuery().contains("method=getMetadata"));
    assertTrue("Query string should contain correct metadata field names", captor.getValue().getQuery().contains
        ("keys=coin:oauth:secret"));
    assertTrue("Query string should contain correct user", captor.getValue().getQuery().contains("userid=user"));
  }

  @Test
  public void getEntityIdsByMetadata() {
    ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
    final List<String> ids = new ArrayList<String>(Arrays.asList("id1", "id2"));
    when(restTemplate.getForObject((URI) anyObject(), eq(List.class))).thenReturn(ids);
    List<String> result = janusRestClient.getEntityIdsByMetaData(Janus.Metadata.OAUTH_CONSUMERKEY, "foobar");
    assertEquals("id1", result.get(0));
    assertEquals("id2", result.get(1));

    verify(restTemplate).getForObject(captor.capture(), eq(List.class));
    assertTrue("Query string should contain correct key (the consumer key metadata name)",
        captor.getValue().getQuery().contains("key=coin:gadgetbaseurl"));
    assertTrue("Query string should contain correct value (the consumer key metadata value)",
        captor.getValue().getQuery().contains("value=foobar"));
    assertTrue("Query string should contain correct method", captor.getValue().getQuery().contains
        ("method=findIdentifiersByMetadata"));
    assertTrue("Query string should contain correct user", captor.getValue().getQuery().contains("userid=user"));

  }
}
