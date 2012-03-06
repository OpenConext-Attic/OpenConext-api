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

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.ui.ModelMap;

/**
 * {@link Test} for {@link JSController}
 *
 */
public class JSControllerTest extends AbstractControllerTest{

  /**
   * Test method for {@link nl.surfnet.coin.portal.control.JSController#js(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}.
   */
  @Test
  public void testJs() {
    ModelMap modelMap = getModelMap();
    new JSController().js(modelMap, getRequest());
    //see setup in super class for the ID of the logged in person
    assertEquals(modelMap.get("viewerId"), "1");
    assertEquals(modelMap.get("ownerId"), "1");
  }

}
