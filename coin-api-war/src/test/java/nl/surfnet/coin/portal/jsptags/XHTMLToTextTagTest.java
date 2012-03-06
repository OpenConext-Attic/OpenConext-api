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


package nl.surfnet.coin.portal.jsptags;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockBodyContent;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.jsp.tagext.TagSupport;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;


/**
 * Test for {@link XHTMLToTextTag}
 */
public class XHTMLToTextTagTest {
  private XHTMLToTextTag xhtmlToTextTag;
  private MockServletContext mockServletContext;
  private MockPageContext mockPageContext;
  private MockBodyContent mockBodyContent;
  private WebApplicationContext mockWebApplicationContext;

  private static final String htmlStr = "</p><DIV class=item>\n" +
      "<H2>DIENSTEN EN INNOVATIES</H2>\n" +
      "<P>SURFnet zorgt dat onderzoekers, docenten en studenten eenvoudig en krachtig samen kunnen werken met behulp van ICT. Bekijk het complete <A id=\"SURFnet dienstverlening\" title=\"Alle diensten en innovaties van SURFnet\" href=\"/nl/organisatie/Dienstverleningsoverzicht/Pages/Dienstverleningsoverzicht.aspx\" target=\"\"><STRONG>dienstverleningsoverzicht</STRONG></A> of kies direct: <BR><BR>\n" +
      "<H3>Hybride end-to-end netwerk</H3><IMG style=\"BORDER-BOTTOM: 0px solid; BORDER-LEFT: 0px solid; BORDER-TOP: 0px solid; BORDER-RIGHT: 0px solid\" alt=\"Hybride Netwerk\" align=left src=\"/SURFnet%20imagebank/Logos/netwerk2011BL_65x62.jpg\" />Beschik over snel en veilig internet via het innovatieve netwerk van SURFnet:<BR><BR><A id=SURFinternet title=SURFinternet href=\"/nl/Hybride_netwerk/SURFinternet/Pages/ip-aansluiting.aspx\" target=\"\">SURFinternet </A>/ <A id=SURFlichtpaden title=SURFlichtpaden href=\"/nl/Hybride_netwerk/SURFlichtpaden/Pages/lichtpaden.aspx\" target=\"\">SURFlichtpaden </A>/ <A id=SURFdomeinnamen title=SURFdomeinnamen href=\"/nl/Hybride_netwerk/SURFdomeinen/Pages/domeinnamen.aspx\" target=\"\">SURFdomeinen </A>/ <A id=SURFinternetpinnen title=SURFinternetpinnen href=\"/nl/Hybride_netwerk/SURFinternetpinnen/Pages/pinnen.aspx\" target=\"\">SURFinternetpinnen </A>/&nbsp;<A id=\"Hybride end-to-end netwerk\" title=\"Hybride end-to-end netwerk\" href=\"http://www.surfnet.nl/nl/Hybride_netwerk/Pages/default.aspx\" target=\"\">meer</A> <BR><BR>\n" +
      "<H3>&nbsp;</H3>\n" +
      "<H3>Grensverleggende samenwerkingsomgeving</H3>\n" +
      "<P><IMG style=\"BORDER-BOTTOM: 0px solid; BORDER-LEFT: 0px solid; BORDER-TOP: 0px solid; BORDER-RIGHT: 0px solid\" alt=\"Grensverleggende samenwerkingsomgeving\" align=left src=\"/SURFnet%20imagebank/Logos/GSO65x75.jpg\" />Online samenwerken wordt steeds gemakkelijker. Informatie delen, vergaderen, mailen, afspraken maken en meer:<BR><BR><A id=SURFmedia title=SURFmedia href=\"/nl/Samenwerkingsomgeving/surfmedia\" target=\"\">SURFmedia </A>/ <A id=SURFcontact title=SURFcontact href=\"/nl/Samenwerkingsomgeving/surfcontact\" target=\"\">SURFcontact </A>/ <A id=\"Online applicaties\" title=\"Online applicaties\" href=\"http://www.surfnet.nl/nl/Samenwerkingsomgeving/Pages/Online_applicaties.aspx\" target=\"\">Online applicaties</A> / <A id=\"Unified Communications\" title=\"Unified Communications\" href=\"/nl/Samenwerkingsomgeving/Pages/UnifiedCommunications.aspx\" target=\"\">Unified Communications </A>/&nbsp;<A title=\"\" href=\"/nl/Samenwerkingsomgeving/surfconext/Pages/ProjectCOIN.aspx\" target=\"\">SURFconext</A>&nbsp;/ <A id=\"Grensverleggende samenwerkingsomgeving\" title=\"Grensverleggende samenwerkingsomgeving\" href=\"http://www.surfnet.nl/nl/Samenwerkingsomgeving/Pages/default.aspx\" target=\"\">meer</A></P>\n" +
      "</div>";
  private static final String textVersion = "DIENSTEN EN INNOVATIES " +
      "SURFnet zorgt dat onderzoekers, docenten en studenten eenvoudig en krachtig samen kunnen werken met behulp van ICT. Bekijk het complete dienstverleningsoverzicht of kies direct:  " +
      "Hybride end-to-end netwerkBeschik over snel en veilig internet via het innovatieve netwerk van SURFnet:SURFinternet / SURFlichtpaden / SURFdomeinen / SURFinternetpinnen /&nbsp;meer  " +
      "&nbsp; " +
      "Grensverleggende samenwerkingsomgeving " +
      "Online samenwerken wordt steeds gemakkelijker. Informatie delen, vergaderen, mailen, afspraken maken en meer:SURFmedia / SURFcontact / Online applicaties / Unified Communications /&nbsp;SURFconext&nbsp;/ meer";

  private static final String trimmedTextVersion = "DIENSTEN EN INNOVATIES SURFnet zorgt dat onderzoekers, docenten en studenten eenvoudig en krachtig samen kunnen werken met behulp van ICT. Bekijk het...";

  @Test
  public void testTextFromHTMLFragment() throws Exception {
    assertEquals(textVersion, XHTMLToTextTag.getTextFromHTMLFragment(htmlStr, 0));
  }

  @Test
  public void testTrimmedTextFromHTMLFragment() throws Exception {
    assertEquals(trimmedTextVersion, XHTMLToTextTag.getTextFromHTMLFragment(htmlStr, 150));
  }

  @Test
  public void testStrippedTextOnPageContext() throws Exception {

    mockBodyContent = new MockBodyContent(htmlStr, mockPageContext.getOut());

    xhtmlToTextTag.setBodyContent(mockBodyContent);
    xhtmlToTextTag.setVar("var");

    replayAllMocks();

    int tagReturnValue = xhtmlToTextTag.doEndTag();

    assertEquals(TagSupport.EVAL_PAGE, tagReturnValue);
    assertEquals(textVersion, mockPageContext.getAttribute("var"));
    verifyAllMocks();
  }

  @Test
  public void testEmptyFragment() throws Exception {
    mockBodyContent = new MockBodyContent("    ", mockPageContext.getOut());

    xhtmlToTextTag.setBodyContent(mockBodyContent);
    xhtmlToTextTag.setVar("var");

    replayAllMocks();

    int tagReturnValue = xhtmlToTextTag.doEndTag();

    assertEquals(TagSupport.EVAL_PAGE, tagReturnValue);
    assertEquals("", mockPageContext.getAttribute("var"));
    verifyAllMocks();
  }

  @Test
  public void testRemovePageEditedBy() throws IOException {
    String htmlFragment = IOUtils.toString(new ClassPathResource("rss-sniplet.txt").getInputStream());
    String stripped = XHTMLToTextTag.getTextFromHTMLFragment(htmlFragment, Integer.MAX_VALUE -1);
    assertEquals(-1,stripped.indexOf("edited"));
  }
  
  private void replayAllMocks() {
    replay(mockWebApplicationContext);
  }

  private void verifyAllMocks() {
    verify(mockWebApplicationContext);
  }

  @Before
  public void setUp() throws Exception {
    // Create the mock servlet context
    mockServletContext = new MockServletContext();

    // Create the mock Spring Context so that we can mock out the calls to getBean in the custom tag
    // Then add the Spring Context to the Servlet Context
    mockWebApplicationContext = createMock(WebApplicationContext.class);
    mockServletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
        mockWebApplicationContext);

    // Create the MockPageContext passing in the mock servlet context created above
    mockPageContext = new MockPageContext(mockServletContext);

    // Create an instance of the custom tag we want to test
    // set it's PageContext to the MockPageContext we created above
    xhtmlToTextTag = new XHTMLToTextTag();
    xhtmlToTextTag.setPageContext(mockPageContext);

    // Whenever you make a call to the doStartTag() method on the custom tag it calls getServletContext()
    // on the WebApplicationContext.  So to avoid having to put this expect statement in every test
    expect(mockWebApplicationContext.getServletContext()).andReturn(mockServletContext).anyTimes();
  }


}
