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

import nl.surfnet.coin.portal.interceptor.LoginInterceptor;
import org.junit.Before;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.stubbing.Answer;
import org.opensocial.models.Group;
import org.opensocial.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Base class for testing {@link Controller} instances
 * 
 */
public abstract class AbstractControllerTest {

  private MockHttpServletRequest request;
  private ModelMap modelMap;

  /**
   * Autowire all dependencies with a annotation autowired with a mock that does
   * nothing
   * 
   * @param target
   *          the controller
   */
  protected void autoWireRemainingResources(Object target) throws Exception {
    Class<? extends Object> clazz = target.getClass();
    while (!clazz.equals(Object.class)) {
      doAutoWireRemainingResources(target, clazz.getDeclaredFields());
      clazz = clazz.getSuperclass();
    }
  }

  protected Returns getGroupReturn() {
    List<Group> groups = new ArrayList<Group>();
    Group group = new Group();
    group.setField("id", "dummy-team");
    groups.add(group);
    return new Returns(groups);
  }

  protected Returns getAllGroups() {
    List<Group> groups = new ArrayList<Group>();
    Group group = new Group();
    group.setField("id", "dummy-team");
    groups.add(group);
    group.setField("id", "funny-team");
    groups.add(group);
    return new Returns(groups);
  }

  private void doAutoWireRemainingResources(Object target, Field[] fields)
      throws IllegalAccessException {
    for (Field field : fields) {
      ReflectionUtils.makeAccessible(field);
      if (field.getAnnotation(Autowired.class) != null
          && field.get(target) == null) {
        field.set(target, mock(field.getType(), new DoesNothing()));
      }
    }
  }

  /**
   * 
   * @param target
   *          the controller
   * @param answer
   *          the answer to return on method invocations
   * @param interfaceClass
   *          the class to mock
   */
  @SuppressWarnings("unchecked")
  protected void autoWireMock(Object target, Answer answer, Class interfaceClass)
      throws Exception {
    Object mock = mock(interfaceClass, answer);
    autoWireMock(target, mock, interfaceClass);
  }

  /**
   * 
   * @param target
   *          the controller
   * @param mock
   *          the mock to return on method invocations
   * @param interfaceClass
   *          the class to mock
   */
  @SuppressWarnings("unchecked")
  protected void autoWireMock(Object target, Object mock, Class interfaceClass)
      throws Exception {
    boolean found = doAutoWireMock(target, mock, interfaceClass, target
        .getClass().getDeclaredFields());
    if (!found) {
      doAutoWireMock(target, mock, interfaceClass, target.getClass()
          .getSuperclass().getDeclaredFields());
    }
  }

  private boolean doAutoWireMock(Object target, Object mock,
      Class interfaceClass, Field[] fields) throws IllegalAccessException {
    boolean found = false;
    for (Field field : fields) {
      if (field.getType().equals(interfaceClass)) {
        ReflectionUtils.makeAccessible(field);
        field.set(target, mock);
        found = true;
        break;
      }
    }
    return found;
  }

  /**
   * Put the Groups and Person in the session
   * 
   * @param request
   *          the HttpServletRequest
   */
  private void setUpSession(HttpServletRequest request) {
    HttpSession session = request.getSession(true);

    Person person = new Person();
    person.setField("id", "1");
    person.setField("displayName", "test");
    Map<String, String> nameMap = new HashMap<String, String>();
    nameMap.put("givenName", "given");
    nameMap.put("familyName", "last");
    nameMap.put("formatted", "given last");
    person.setField("name", nameMap);
    person.setField("emails", Collections.singletonList("test@surfnet.nl"));
    session.setAttribute(LoginInterceptor.PERSON_SESSION_KEY, person);
    session.setAttribute(LoginInterceptor.USER_STATUS_SESSION_KEY, "member");
    ArrayList<Person> persons = new ArrayList<Person>();
    persons.add(person);
    session.setAttribute(LoginInterceptor.INVITEES, persons);
  }

  public Person getPerson() {
    return (Person) getRequest().getSession().getAttribute(
        LoginInterceptor.PERSON_SESSION_KEY);
  }

  @Before
  public void setup() throws Exception {
    this.request = new MockHttpServletRequest();
    this.modelMap = new ModelMap();
    setUpSession(request);
  }

  /**
   * @return the request
   */
  protected MockHttpServletRequest getRequest() {
    return request;
  }

  /**
   * @return the modelMap
   */
  protected ModelMap getModelMap() {
    return modelMap;
  }
}
