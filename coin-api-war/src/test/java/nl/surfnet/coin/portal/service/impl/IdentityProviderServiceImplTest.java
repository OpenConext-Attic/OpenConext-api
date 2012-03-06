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


package nl.surfnet.coin.portal.service.impl;

import nl.surfnet.coin.mock.AbstractMockHttpServerTest;
import nl.surfnet.coin.portal.domain.IdentityProvider;
import nl.surfnet.coin.portal.util.CoinEnvironment;
import nl.surfnet.coin.shared.service.ErrorMessageMailer;
import nl.surfnet.coin.shared.service.MockJavaMailSender;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * {@link Test} for {@link IdentityProviderServiceImpl}
 * 
 */
public class IdentityProviderServiceImplTest extends AbstractMockHttpServerTest {

  private IdentityProviderServiceImpl identityProviderService;

  private CoinEnvironment environment;

  @Before
  public void setup() {

    identityProviderService = new IdentityProviderServiceImpl();

    environment = new CoinEnvironment();
    // on this address listens the MockHttpServer
    environment.setIdpMetadataUrl("http://localhost:8088/");

    identityProviderService.setEnvironment(environment);

    ErrorMessageMailer errorMessageMailer = new ErrorMessageMailer();
    errorMessageMailer.setMailSender(new MockJavaMailSender());
    identityProviderService.setErrorMessageMailer(errorMessageMailer);
    // mock the response
    setResponseResource(new ClassPathResource("idp-metadata.xml"));
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.service.impl.IdentityProviderServiceImpl#getIdps()}
   * .
   * @throws Exception if anything goes wrong
   */
  @Test
  public void testGetIdps() throws Exception {
    // mock the response
    List<IdentityProvider> idps = identityProviderService.getIdps();

    assertEquals(3, idps.size());

    // Get the first Identity Provider
    IdentityProvider idp = idps.get(0);

    assertEquals("NL", idp.getCountry());
    assertEquals("SURFguest (TEST) English", idp.getDisplayName("en"));
    assertEquals("SURFguest (TEST) Nederlands", idp.getDisplayName("nl"));
    assertEquals("SURFnetGuests", idp.getEntityId());
    assertEquals("52.091175137667314, 5.111839771270752",
        idp.getGeoLocationHint());
    assertEquals("https://www.surfguest.nl/img/surfnet_logo.gif", idp.getLogo());
    assertEquals(null, idp.getDisplayName("ru"));
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.service.impl.IdentityProviderServiceImpl#getIdps()}
   * .
   * @throws Exception if anything goes wrong
   */
  @Test
  public void testGetIdpsExtended() throws Exception {

    List<IdentityProvider> mockIdps = new ArrayList<IdentityProvider>();

    IdentityProvider idp1 = new IdentityProvider();
    idp1.setDisplayName("en", "SURFguest (TEST) English");
    idp1.setDisplayName("nl", "SURFguest (TEST) Nederlands");
    idp1.setEntityId("SURFnetGuests");
    idp1.setGeoLocationHint("52.091175137667314, 5.111839771270752");
    idp1.setLogo("https://www.surfguest.nl/img/surfnet_logo.gif");

    mockIdps.add(idp1);

    IdentityProvider idp2 = new IdentityProvider();
    idp2.setDisplayName("en", "SURFnet BV");
    idp2.setDisplayName("nl", "SURFnet BV");
    idp2.setEntityId("SURFnet%20BV");
    idp2.setGeoLocationHint("50.76311927442563, 6.002869606018066");
    idp2.setLogo("https://www.surfguest.nl/img/surfnet_logo.gif");

    mockIdps.add(idp2);

    IdentityProvider idp3 = new IdentityProvider();
    idp3.setDisplayName("en", "University of Amsterdam (acc)");
    idp3.setDisplayName("nl", "Universiteit van Amsterdam (acc)");
    idp3.setEntityId("https://cas-acc.ic.uva.nl/cas");
    idp3.setGeoLocationHint("52.372035959714744, 4.931488037109375");
    idp3.setLogo("http://www.academictransfer.com/media/logos_wide/2009/09/22/university_of_amsterdam_uva_logo_312x105_png_312x105_q85.jpg");

    mockIdps.add(idp3);

    // mock the response
    setResponseResource(new ClassPathResource("idp-metadata.xml"));
    List<IdentityProvider> idps = identityProviderService.getIdps();

    for (int i = 0; i < idps.size(); i++) {
      // Get the first Identity Provider
      IdentityProvider idp = idps.get(0);
      IdentityProvider mockIdp = mockIdps.get(0);

      assertEquals(mockIdp.getCountry(), idp.getCountry());
      assertEquals(mockIdp.getDisplayName("en"), idp.getDisplayName("en"));
      assertEquals(mockIdp.getDisplayName("nl"), idp.getDisplayName("nl"));
      assertEquals(mockIdp.getEntityId(), idp.getEntityId());
      assertEquals(mockIdp.getGeoLocationHint(), idp.getGeoLocationHint());
      assertEquals(mockIdp.getLogo(), idp.getLogo());
      assertEquals(null, idp.getDisplayName("ru"));
    }
  }

  @Test
  public void getIdpsWithWrongIdpMetaData_ShouldSkip() throws Exception {
    setResponseResource(new ClassPathResource("idp-metadata-missing.xml"));

    List<IdentityProvider> idps = identityProviderService.getIdps();

    assertEquals("2 Idps", 2, idps.size());
    assertEquals("SURFnetGuests", idps.get(0).getEntityId());
    assertEquals("https%3A%2F%2Fcas-acc.ic.uva.nl%2Fcas", idps.get(1)
        .getEntityId());
  }

  @Test
  public void getIdpWithNullPointer() throws Exception {
    setResponseResource(new ClassPathResource("idp-metadata-nullpointer.xml"));

    List<IdentityProvider> idps = identityProviderService.getIdps();
    assertEquals(14, idps.size());

  }
  
  @Test
  public void getIdpsWithWrongIdpMetaDataMissingAttributes() throws Exception {
    setResponseResource(new ClassPathResource("idp-metadata-not-complete.xml"));

    List<IdentityProvider> idps = identityProviderService.getIdps();

    IdentityProvider idp = idps.get(0);
    
    assertEquals(1, idps.size());
    assertNull(idp.getLogo());
    assertNull(idp.getGeoLocationHint());
    assertEquals(true, CollectionUtils.isEmpty(idp.getKeywords()));
    

  }
  @Test
  public void getIdpWithSara() throws Exception {
    setResponseResource(new ClassPathResource("jsons/sara-metadata.json"));

    List<IdentityProvider> idps = identityProviderService.getIdps();
    assertEquals(1, idps.size());

  }
}
