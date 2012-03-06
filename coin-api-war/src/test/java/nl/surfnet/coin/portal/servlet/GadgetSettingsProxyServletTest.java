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


package nl.surfnet.coin.portal.servlet;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

import javax.servlet.ServletException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

/**
 * Test class for {@link GadgetSettingsProxyServlet}
 */
public class GadgetSettingsProxyServletTest {

  @Test
  public void testFailureInit() throws Exception {
    MockServletConfig mockServletConfig = new MockServletConfig();
    mockServletConfig.addInitParameter("gModulesUrl", "fake");

    GadgetSettingsProxyServlet servlet = new GadgetSettingsProxyServlet();
    try {
      servlet.init(mockServletConfig);
      fail("Fake is not a valid URL");
    } catch (ServletException e) {
      assertNull("gModulesUrl is not set", servlet.getGModulesUrl());
    }

  }

  @Test
  public void testInit() throws Exception {
    MockServletConfig mockServletConfig = new MockServletConfig();
    String gModulesUrl = "http://www.gmodules.com/ig/gadgetsettings";
    mockServletConfig.addInitParameter("gModulesUrl", gModulesUrl);

    GadgetSettingsProxyServlet servlet = new GadgetSettingsProxyServlet();

    servlet.init(mockServletConfig);
    assertEquals(gModulesUrl, servlet.getGModulesUrl());
  }

  @Test
  public void testHttpQueryString() throws Exception {
    MockServletConfig servletConfig = new MockServletConfig();
    String gModulesUrl = "http://www.gmodules.com/ig/gadgetsettings";
    servletConfig.addInitParameter("gModulesUrl", gModulesUrl);

    GadgetSettingsProxyServlet servlet = new GadgetSettingsProxyServlet();
    servlet.init(servletConfig);

    assertNotNull(servlet.getGadgetProxyUrl());

    String queryString = "mid=0&output=js&up_fixed_width=320&up_autostart=0&up_url=&" +
        "up_name=nl%3Asurfnet%3Adiensten%3Ademo_team&" +
        "aap=noot&aap=mies&aap=vuur&" +
        "url=https://www.integration.surfmedia.nl/gadget/xml/player";

    String responseString = "mid=0&output=js&up_fixed_width=320&up_autostart=0&up_url=&" +
        "up_name=nl%3Asurfnet%3Adiensten%3Ademo_team&" +
        "aap=noot&aap=mies&aap=vuur&" +
        "url=" + servlet.getGadgetProxyUrl()
        + "aHR0cHM6Ly93d3cuaW50ZWdyYXRpb24uc3VyZm1lZGlhLm5sL2dhZGdldC94bWwvcGxheWVy.xml";

    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.setQueryString(queryString);
    mockRequest.addParameter("mid", "0");
    mockRequest.addParameter("output", "js");
    mockRequest.addParameter("up_fixed_width", "320");
    mockRequest.addParameter("up_autostart", "0");
    mockRequest.addParameter("up_url", "");
    mockRequest.addParameter("up_name", "nl:surfnet:diensten:demo_team");
    mockRequest.addParameter("aap", new String[]{"noot", "mies", "vuur"});
    mockRequest.addParameter("url", "https://www.integration.surfmedia.nl/gadget/xml/player");

    MockHttpServletResponse mockResponse = new MockHttpServletResponse();
    assertEquals(responseString, servlet.getQueryString(mockRequest, mockResponse));

  }


}
