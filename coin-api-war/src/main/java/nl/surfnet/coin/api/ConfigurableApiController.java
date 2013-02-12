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

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.service.ConfigurableGroupProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller for the mock REST interface..
 */
@Controller
@RequestMapping(value = "configure")
public class ConfigurableApiController {

  private static Logger LOG = LoggerFactory.getLogger(ConfigurableApiController.class);

  @Resource(name="mockService")
  private ConfigurableGroupProvider configurableGroupProvider;

  @RequestMapping(value = { "/person" }, method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void addPerson(@RequestBody
  Person person) {
    LOG.info("Request to add Person");
    configurableGroupProvider.addPerson(person);
  }

  @RequestMapping(value = { "/group" }, method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void addGroup(@RequestBody
  Group20 group) {
    LOG.info("Request to add Group {}", group);
    configurableGroupProvider.addGroup(group);
  }

  @RequestMapping(value = { "/person/{userId:.+}/{groupId:.+}" }, method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void addPersonToGroup(@PathVariable("userId")
  String personId, @PathVariable("groupId")
  String groupId) {
    LOG.info("Request to add Person {} to Group {}", new Object[] { personId, groupId });
    configurableGroupProvider.addPersonToGroup(personId, groupId);

  }

  @RequestMapping(value = { "/reset" }, method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void reset() {
    LOG.info("Request to reset the state for Mock External Group Provider");
    configurableGroupProvider.reset();
  }

  @RequestMapping(value = { "/sleep/{millSeconds}" }, method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void sleep(@PathVariable("millSeconds") long millSeconds) {
    LOG.info("Request to configure Mock External Group Provider for a sleep {} time", millSeconds);
    configurableGroupProvider.sleep(millSeconds);
  }

}
