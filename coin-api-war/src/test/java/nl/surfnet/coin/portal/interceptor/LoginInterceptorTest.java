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


package nl.surfnet.coin.portal.interceptor;

import nl.surfnet.coin.opensocial.service.GroupService;
import nl.surfnet.coin.opensocial.service.PersonService;
import nl.surfnet.coin.portal.util.CoinEnvironment;
import nl.surfnet.coin.shared.service.ErrorMessageMailer;
import nl.surfnet.coin.shared.service.MockJavaMailSender;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link LoginInterceptor}
 */
public class LoginInterceptorTest {

  @Test
  public void testPreHandle() throws Exception {
    String remoteUser = "urn:collab:person:surfnet.nl:hansz";

    LoginInterceptor interceptor = new LoginInterceptor();
    ErrorMessageMailer errorMessageMailer = new ErrorMessageMailer();
    errorMessageMailer.setMailSender(new MockJavaMailSender());
    interceptor.setErrorMessageMailer(errorMessageMailer);

    PersonService personService = mock(PersonService.class);
    when(personService.getPerson(remoteUser, remoteUser)).thenReturn(new org.opensocial.models.Person());

    GroupService groupService = mock(GroupService.class);
    List<org.opensocial.models.Group> groups = new ArrayList<org.opensocial.models.Group>();
    groups.add(new org.opensocial.models.Group());
    when(groupService.getGroups(remoteUser)).thenReturn(groups);

    interceptor.setPersonService(personService);
    interceptor.setCoinEnvironment(new CoinEnvironment());

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("REMOTE_USER", remoteUser);
    request.addHeader("coin-user-status", "member");
    MockHttpServletResponse response = new MockHttpServletResponse();
    boolean loggedIn = interceptor.preHandle(request, response, null);
    assertTrue(loggedIn);
    Assert.assertNotNull(request.getSession().getAttribute("person"));
  }

  @Test
  public void testLoginFail() throws Exception {
    String remoteUser = "urn:collab:person:surfnet.nl:hansz";

    LoginInterceptor interceptor = new LoginInterceptor();
    ErrorMessageMailer errorMessageMailer = new ErrorMessageMailer();
    errorMessageMailer.setMailSender(new MockJavaMailSender());
    interceptor.setErrorMessageMailer(errorMessageMailer);

    PersonService personService = mock(PersonService.class);
    when(personService.getPerson(remoteUser, remoteUser)).thenReturn(null);


    interceptor.setPersonService(personService);
    interceptor.setCoinEnvironment(new CoinEnvironment());

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("REMOTE_USER", remoteUser);
    request.addHeader("coin-user-status", "member");
    MockHttpServletResponse response = new MockHttpServletResponse();
    boolean loggedIn = false;
    try {
      loggedIn = interceptor.preHandle(request, response, null);
      fail("Unknown user " + remoteUser);
    } catch (ServletException e) {
      assertFalse(loggedIn);
    }
  }
}
