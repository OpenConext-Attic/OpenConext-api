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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

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
    janusRestClient.getOauthSecretByClientId("foo");
    verify(restTemplate).getForObject(captor.capture(), eq(String.class));
    assertTrue("Query string should contain correct spentityid (the client id)", captor.getValue().getQuery().contains("spentityid=foo"));
    assertTrue("Query string should contain correct method", captor.getValue().getQuery().contains("method=getSpList"));
    assertTrue("Query string should contain correct user", captor.getValue().getQuery().contains("userid=user"));
  }
}
