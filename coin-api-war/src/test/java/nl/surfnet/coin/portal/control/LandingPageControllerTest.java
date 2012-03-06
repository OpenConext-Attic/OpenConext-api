/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
* 
* http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/


package nl.surfnet.coin.portal.control;

import com.sun.syndication.feed.synd.SyndFeed;
import nl.surfnet.coin.portal.domain.IdentityProvider;
import nl.surfnet.coin.portal.domain.TextContent;
import nl.surfnet.coin.portal.service.IdentityProviderService;
import nl.surfnet.coin.portal.service.TextContentService;
import nl.surfnet.coin.portal.util.CoinEnvironment;

import org.hibernate.Criteria;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.Returns;
import org.springframework.web.servlet.LocaleResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * {@link Test} for {@link LandingPageController}
 * 
 */
public class LandingPageControllerTest extends AbstractControllerTest {
  
  private LandingPageController landingPageController = new LandingPageController();
  
  /**
   * Test the happy flow without exceptions
   * 
   * @throws Exception
   *           in case of an unexpected Exception
   */
  @Test
  public void testStartHappyFlow() throws Exception {
    IdentityProvider idp = new IdentityProvider();
    idp.setDefaultLocale(Locale.ENGLISH);
    idp.setGeoLocationHint("52.091175137667314, 5.111839771270752");
    idp.setDisplayName("nl", "Test IDP Nederlands");
    idp.setDisplayName("en", "Test IDP English");
    idp.setEntityId("entityID");
    idp.setLogo("https://example.com/logo.png");
    
    List<IdentityProvider> idps = new ArrayList<IdentityProvider>();
    idps.add(idp);
    
    autoWireMock(landingPageController, new Returns(idps), IdentityProviderService.class);
    autoWireMock(landingPageController, new Returns(new Locale("nl", "NL")), LocaleResolver.class);
    
    String viewName = landingPageController.start(getModelMap(), getRequest());
    assertEquals("landingpage", viewName);
    
    assertEquals(1, idps.size());
    assertEquals("entityID", idps.get(0).getEntityId());
    assertEquals("Test IDP English", idps.get(0).getDisplayName());
    SyndFeed syndFeed = mock(SyndFeed.class);
    autoWireMock(landingPageController, new Returns(syndFeed), SyndFeed.class);
    List<SyndFeed> feeds = (List<SyndFeed>) getModelMap().get("feeds");
    assertEquals(2, feeds.size());
  }

  @Override
  public void setup() throws Exception {
    super.setup();
    CoinEnvironment env = new CoinEnvironment();
    env.setLandingPageRssFeeds("https://wiki.surfnetlabs.nl/createrssfeed.action?types=page&spaces=conextsupport&title=SURFconext+demo+portal+support&labelString=frontpage&excludedSpaceKeys%3D&sort=modified&maxResults=10&timeSpan=600&showContent=true&confirm=Create+RSS+Feed&showDiff=false&os_authType=basic?os_username=myname&os_password=mypassword,http://www.surfnet.nl/nl/nieuws/_layouts/listfeed.aspx?List=da704a13-ab80-4c2b-b516-a87a9c22acfb&View=df251e26-ed65-4ff4-a62f-4e9d9393ce5f");
    landingPageController.setEnvironment(env);
    autoWireMock(landingPageController, new Returns(new TextContent()), TextContentService.class);
  }
}
