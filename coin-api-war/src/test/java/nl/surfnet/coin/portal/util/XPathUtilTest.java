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


package nl.surfnet.coin.portal.util;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * {@link Test} for {@link XPathUtil}
 * 
 */
public class XPathUtilTest {

  private static String testXML = "idp-metadata.xml";
  private static XPathUtil xpathUtil;

  public XPathUtilTest() throws MalformedURLException {
    xpathUtil = new XPathUtil();
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.util.XPathUtil#getNodes(java.io.InputStream, java.util.Map, String)}
   * .
   */
  @Test
  public void testGetNodes() throws XPathExpressionException, IOException,
      SAXException, ParserConfigurationException {
    InputStream in = new ClassPathResource(testXML).getInputStream();

    // Create a map with namespaces
    Map<String, String> namespaces = new HashMap<String, String>();
    namespaces.put("md", "urn:oasis:names:tc:SAML:2.0:metadata");
    namespaces.put("mdui", "urn:oasis:names:tc:SAML:2.0:metadata:ui");

    NodeList nodeList = xpathUtil.getNodes(in, namespaces,
        "/md:EntitiesDescriptor/md:EntityDescriptor/@entityID");
    in.close();
    assertEquals(3, nodeList.getLength());
  }
  
  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.util.XPathUtil#getNodes(org.w3c.dom.Node, java.util.Map, String)}
   * .
   */
  @Test
  public void testGetNodesWithNode() throws XPathExpressionException, IOException,
      SAXException, ParserConfigurationException {
    InputStream in = new ClassPathResource(testXML).getInputStream();

    // Create a map with namespaces
    Map<String, String> namespaces = new HashMap<String, String>();
    namespaces.put("md", "urn:oasis:names:tc:SAML:2.0:metadata");
    namespaces.put("mdui", "urn:oasis:names:tc:SAML:2.0:metadata:ui");

    NodeList nodeList = xpathUtil.getNodes(in, namespaces,
        "/md:EntitiesDescriptor/md:EntityDescriptor/@entityID");
    in.close();
    

    Node nodeIn = nodeList.item(0);
    NodeList nodes = xpathUtil.getNodes(nodeIn, namespaces,
    "/md:EntitiesDescriptor/md:EntityDescriptor[1]//mdui:DisplayName");
    
    assertEquals(2, nodes.getLength());
    assertEquals("SURFguest (TEST) English", nodes.item(0).getFirstChild().getNodeValue());
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.portal.util.XPathUtil#getNode(org.w3c.dom.Node, java.util.Map, String)}
   * .
   */
  @Test
  public void testGetNode() throws XPathExpressionException, IOException,
      SAXException, ParserConfigurationException {
    InputStream in = new ClassPathResource(testXML).getInputStream();

    // Create a map with namespaces
    Map<String, String> namespaces = new HashMap<String, String>();
    namespaces.put("md", "urn:oasis:names:tc:SAML:2.0:metadata");
    namespaces.put("mdui", "urn:oasis:names:tc:SAML:2.0:metadata:ui");

    NodeList nodeList = xpathUtil.getNodes(in, namespaces,
        "/md:EntitiesDescriptor/md:EntityDescriptor");
    in.close();

    Node nodeIn = nodeList.item(0);

    Node node = xpathUtil.getNode(nodeIn, namespaces,
        "/md:EntitiesDescriptor/md:EntityDescriptor[3]//mdui:Logo");

    assertEquals(3, node.getAttributes().getLength());
    assertEquals(
        "http://www.academictransfer.com/media/logos_wide/2009/09/22/university_of_amsterdam_uva_logo_312x105_png_312x105_q85.jpg",
        StringUtils.trimWhitespace(node.getFirstChild().getNodeValue()));
  }
  
  

}
