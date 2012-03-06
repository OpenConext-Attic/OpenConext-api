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

import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.assertEquals;

/**
 * Test for {@link GadgetDefinitionProxyServlet}
 */
public class GadgetDefinitionProxyServletTest {

  @Test
  public void testGetDestination() throws Exception {
    GadgetDefinitionProxyServlet servlet = new GadgetDefinitionProxyServlet();

    MockHttpServletRequest request = new MockHttpServletRequest();
    // Base64 encoded version of: https://www.integration.surfmedia.nl/gadget/xml/player
    request.setPathInfo("/aHR0cHM6Ly93d3cuaW50ZWdyYXRpb24uc3VyZm1lZGlhLm5sL2dhZGdldC94bWwvcGxheWVy.xml");

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockServletConfig servletConfig = new MockServletConfig();

    servlet.init(servletConfig);
    String destination = servlet.getDestination(request, response);
    assertEquals("https://www.integration.surfmedia.nl/gadget/xml/player", destination);
  }

  @Test
  public void testFailToReturnDestination() throws Exception {
    GadgetDefinitionProxyServlet servlet = new GadgetDefinitionProxyServlet();

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockServletConfig servletConfig = new MockServletConfig();

    servlet.init(servletConfig);
    servlet.doGet(request, response);
    assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());

  }
}
