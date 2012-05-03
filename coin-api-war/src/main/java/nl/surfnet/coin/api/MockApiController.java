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

package nl.surfnet.coin.api;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import nl.surfnet.coin.api.service.GroupService;
import nl.surfnet.coin.api.service.MockServiceImpl;
import nl.surfnet.coin.api.service.PersonService;
import nl.surfnet.coin.eb.EngineBlock;

/**
 * Controller for the mock REST interface.
 */
@Controller
@RequestMapping(value = "mock10/social/rest")
@SuppressWarnings("unchecked")
public class MockApiController extends ApiController {

  private static Logger LOG = LoggerFactory.getLogger(MockApiController.class);

  @Value("${mock-api-enabled}")
  private boolean mockApiEnabled;

  public MockApiController() {
    MockServiceImpl impl = new MockServiceImpl();
    this.personService = impl;
    this.groupProviderConfiguration = impl;
    this.groupService = impl;
  }
  
  @Override
  public void invariant() {
    if (!this.mockApiEnabled) {
      throw new RuntimeException("Mock API not enabled");
    }
  }

//  @Resource(name = "mockService")
//  public void setPersonService(PersonService personService) {
//    this.personService = personService;
//  }
//
//  @Resource(name = "mockService")
//  public void setGroupService(GroupService groupService) {
//    this.groupService = groupService;
//  }
//
  @Resource(name = "engineBlock")
  public void setEngineBlock(EngineBlock engineBlock) {
    this.engineBlock = engineBlock;
  }

  public void setMockApiEnabled(boolean mockApiEnabled) {
    this.mockApiEnabled = mockApiEnabled;
  }
}
