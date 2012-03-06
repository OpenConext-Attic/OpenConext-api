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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;
import nl.surfnet.coin.portal.domain.Gadget;
import nl.surfnet.coin.portal.domain.Tab;
import nl.surfnet.coin.portal.service.GadgetService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * {@link Test} for {@link PrefsController}
 *
 */
public class PrefsControllerTest extends AbstractControllerTest {

  private static final String GADGET_ID = "1";
  private PrefsController controller = new PrefsController();
  private Gadget gadget;
  private Gadget otherpersonGadget;

  @Before
  @Override
  public void setup() throws Exception{
    super.setup();

    gadget = new Gadget();
    Tab tab = new Tab();
    tab.setOwner("1");
    gadget.setTab(tab);
    gadget.setId(Long.parseLong(GADGET_ID));

    otherpersonGadget = new Gadget();
    Tab tab2 = new Tab();
    tab2.setOwner("2");
    otherpersonGadget.setTab(tab2);
    otherpersonGadget.setId(76L);

    GadgetService gadgetService = mock(GadgetService.class);
    when(gadgetService.findById(gadget.getId())).thenReturn(gadget);
    when(gadgetService.findById(otherpersonGadget.getId())).thenReturn(otherpersonGadget);

    autoWireMock(controller, gadgetService, GadgetService.class);
  }

  @Test
  public void testSaveNormal() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("gadgetId", GADGET_ID);
    request.setParameter("up_key1", "value1");
    request.setParameter("up_key2", "value2");

    controller.savePrefs(getModelMap(), request);
    Assert.assertEquals(gadget.getPrefs(), "key1=value1&key2=value2");
  }

  @Test
  public void testSaveEmpty() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("gadgetId", GADGET_ID);
    request.setParameter("up_key1", "");
    request.setParameter("up_key2", "");

    controller.savePrefs(getModelMap(), request);
    Assert.assertEquals(gadget.getPrefs(), "key1=&key2=");
  }

  @Test
  public void testSaveNone() throws Exception {
    getRequest().setParameter("gadgetId", GADGET_ID);

    controller.savePrefs(getModelMap(), getRequest());
    Assert.assertEquals(gadget.getPrefs(), "");
  }

  @Test
  public void testSaveInvalid() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("gadgetId", GADGET_ID);
    request.setParameter("key1", "value1");
    request.setParameter("up_key2", "value2");
    request.setParameter("key3", "value3");

    controller.savePrefs(getModelMap(), request);
    Assert.assertEquals(gadget.getPrefs(), "key2=value2");
  }
  
  @Test
  public void testSaveEscape() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("gadgetId", GADGET_ID);
    request.setParameter("up_key1", "a=b");
    
    controller.savePrefs(getModelMap(), request);
    Assert.assertEquals(gadget.getPrefs(), "key1=a%3Db");
  }

  @Test
  public void testSaveMalicious() throws Exception {
    MockHttpServletRequest request = getRequest();
    request.setParameter("gadgetId", "76");
    request.setParameter("up_key1", "value1");
    request.setParameter("up_key2", "value2");

    boolean outcome = controller.savePrefs(getModelMap(), request);
    Assert.assertFalse(outcome);
  }

  @Test
  public void testGet() {
    MockHttpServletRequest request = getRequest();
    gadget.setPrefs("test");
    request.setParameter("gadgetId", GADGET_ID);
    String ret = controller.getPrefs(getModelMap(), request);
    Assert.assertEquals(ret, "test");
  }
}
