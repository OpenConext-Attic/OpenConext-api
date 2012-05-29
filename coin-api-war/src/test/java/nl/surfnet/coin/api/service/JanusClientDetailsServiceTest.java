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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.web.client.RestTemplate;

import nl.surfnet.coin.api.oauth.ExtendedBaseConsumerDetails;
import nl.surfnet.coin.janus.Janus;
import nl.surfnet.coin.janus.Janus.Metadata;
import nl.surfnet.coin.janus.JanusRestClient;
import nl.surfnet.coin.janus.domain.EntityMetadata;
import nl.surfnet.coin.mock.AbstractMockHttpServerTest;
import nl.surfnet.coin.shared.cache.MethodNameAwareCacheKeyGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@Configuration
@EnableCaching
public class JanusClientDetailsServiceTest extends AbstractMockHttpServerTest implements CachingConfigurer{

  private JanusClientDetailsService service;

  @Before
  public void init() throws URISyntaxException {
    service = new JanusClientDetailsService();
    JanusRestClient janus = new JanusRestClient();
    RestTemplate restTemplate = new RestTemplate();
    List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
    converters.add(new MappingJacksonHttpMessageConverter());
    restTemplate.setMessageConverters(converters);
    janus.setJanusUri(new URI("http://localhost:8088/janus/services/rest/"));
    janus.setSecret("secret");
    janus.setUser("user");

    janus.setRestTemplate(restTemplate);
    service.setJanus(janus);
  }

  @Test
  public void loadConsumerByConsumerKeyNoSecretFound() {
    setResponseResource(new ClassPathResource("janus/janus-empty-result.json"));
    ConsumerDetails result = service.loadConsumerByConsumerKey("consumerkey");
    assertNull("service should return null in case consumer key not found.", result);
  }

  @Test
  public void loadClientByConsumerKeyNoSecretFound() {
    super.setResponseResource(new ClassPathResource("janus/janus-empty-result.json"));
    ClientDetails result = service.loadClientByClientId("consumerkey");
    assertNull("service should return null when no  metadata found for consumer.", result);
  }

  @Test
  public void loadConsumerByConsumerKeyHappy() {
    super.setResponseResource(new Resource[] {
        new ClassPathResource("janus/janus-response-consumerkey3-entityid.json"),
        new ClassPathResource("janus/janus-response-consumerkey3-metadata.json") });
    ExtendedBaseConsumerDetails result = (ExtendedBaseConsumerDetails) service
        .loadConsumerByConsumerKey("consumerkey3");
    assertEquals("service should return correct key", "consumerkey3", result.getConsumerKey());
    assertEquals("service should return correct secret", "secret",
        ((SharedConsumerSecret) result.getSignatureSecret()).getConsumerSecret());
    assertEquals("service should return whether client is required to authenticate (so two legged NOT allowed)", true,
        result.isRequiredToObtainAuthenticatedToken());
    assertEquals("app-thumbnail", result.getClientMetaData().getAppThumbNail());
  }

  /**
   * Test to see if the cache works. Especially the fact that we store items in
   * the same cache with the same key for different return Objects:
   * ClientDetails and ConsumerDetails
   * 
   */
  @Test
  public void testCache() throws JsonParseException, JsonMappingException, IOException {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(this.getClass());

    ClientDetailsService clientDetailsService = (ClientDetailsService) ctx.getBean("janusClientDetailsService");
    Janus janus = (Janus) ctx.getBean("janus");

    when(janus.getEntityIdsByMetaData(Metadata.OAUTH_CONSUMERKEY, "consumerkey")).thenReturn(
        Collections.singletonList("sp-entity-id"));
    when(janus.getMetadataByEntityId("sp-entity-id", JanusClientDetailsService.METADATA_TO_FETCH)).thenReturn(
        getMetadata());
    ClientDetails clientDetails = clientDetailsService.loadClientByClientId("consumerkey");
    assertEquals("secret", clientDetails.getClientSecret());

    // when we do this a second time the cache should kick in
    when(janus.getEntityIdsByMetaData(Metadata.OAUTH_CONSUMERKEY, "consumerkey")).thenThrow(
        new RuntimeException("Cache did not kick in"));
    clientDetailsService.loadClientByClientId("consumerkey");

    /*
     * now do the same for the loading of ConsumerDetails (and yes, this lengthy
     * test including the reset is necessary) to make sure we don't hit the
     * cache for loading the ConsumerDetails as we store both in the same cache
     * with potentially the same key (e.g. the consumerkey) resulting in
     * java.lang.ClassCastException:
     * nl.surfnet.coin.api.oauth.ExtendedBaseClientDetails cannot be cast to
     * org.springframework.security.oauth.provider.ConsumerDetails without a
     * custom key generator
     */
    reset(janus);
    when(janus.getEntityIdsByMetaData(Metadata.OAUTH_CONSUMERKEY, "consumerkey")).thenReturn(
        Collections.singletonList("sp-entity-id"));
    when(janus.getMetadataByEntityId("sp-entity-id", JanusClientDetailsService.METADATA_TO_FETCH)).thenReturn(
        getMetadata());

    ConsumerDetailsService consumerDetailsService = (ConsumerDetailsService) clientDetailsService;
    ConsumerDetails consumerDetails = consumerDetailsService.loadConsumerByConsumerKey("consumerkey");
    assertEquals("secret", ((SharedConsumerSecret) consumerDetails.getSignatureSecret()).getConsumerSecret());

    when(janus.getEntityIdsByMetaData(Metadata.OAUTH_CONSUMERKEY, "consumerkey")).thenThrow(
        new RuntimeException("Cache did not kick in"));
    consumerDetailsService.loadConsumerByConsumerKey("consumerkey");
}

  private EntityMetadata getMetadata() {
    EntityMetadata em = new EntityMetadata();
    em.setOauthConsumerSecret("secret");
    return em;
  }

  /*
   * All methods below are necessary for the ApplicationContext to be valid
   */

  @Bean(name = "janusClientDetailsService")
  public JanusClientDetailsService service() {
    return new JanusClientDetailsService();
  }

  @Bean(name = "janus")
  public Janus janus() {
    return mock(Janus.class);
  }


  @Bean
  public CacheManager cacheManager() {
    EhCacheCacheManager cacheManager = new EhCacheCacheManager();
    EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
    factoryBean.setConfigLocation(new ClassPathResource("api-ehcache.xml"));
    try {
      factoryBean.afterPropertiesSet();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    cacheManager.setCacheManager(factoryBean.getObject());
    return cacheManager;
  }

  @Bean
  @Override
  public KeyGenerator keyGenerator() {
    return new MethodNameAwareCacheKeyGenerator();
  }

}
