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
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
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
  public void getOauthSecretByClientId() {
    ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
    final HashMap<String, String> map = new HashMap<String, String>();
    map.put("coin:oauth:consumer_secret", "hissecret");
    when(restTemplate.getForObject((URI) anyObject(), eq(Map.class))).thenReturn(map);
    String result = janusRestClient.getOauthSecretByClientId("foo");
    assertEquals("hissecret", result);
    verify(restTemplate).getForObject(captor.capture(), eq(Map.class));
    assertTrue("Query string should contain correct spentityid (the client id)", captor.getValue().getQuery().contains("entityid=foo"));
    assertTrue("Query string should contain correct method", captor.getValue().getQuery().contains("method=getMetadata"));
    assertTrue("Query string should contain correct user", captor.getValue().getQuery().contains("userid=user"));
  }
}
