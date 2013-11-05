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

import nl.surfnet.coin.api.service.GroupService;
import nl.surfnet.coin.api.service.OpenConextClientDetailsService;
import nl.surfnet.coin.api.service.PersonService;
import nl.surfnet.coin.eb.EngineBlock;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the person REST interface..
 */
@Controller
@RequestMapping(value = "/social/rest")
public class PersonController extends ApiController {

  @Resource(name="ldapService")
  public void setPersonService(PersonService service) {
    this.personService = service;
  }

  @Resource(name="groupService")
  public void setGroupService(GroupService service) {
    this.groupService = service;
  }
  
  @Resource(name="engineBlock")
  public void setEngineBlock(EngineBlock engineBlock) {
    this.engineBlock = engineBlock;
  }
  
  @Resource(name = "groupProviderConfiguration")
  public void setGroupProviderConfiguration(GroupProviderConfiguration groupProviderConfiguration) {
    this.groupProviderConfiguration = groupProviderConfiguration;
  }

  @Resource(name ="janusClientDetailsService")
  public void setJanusClientDetailsService(OpenConextClientDetailsService janusClient) {
    this.janusClientDetailsService = janusClient;
  }

}