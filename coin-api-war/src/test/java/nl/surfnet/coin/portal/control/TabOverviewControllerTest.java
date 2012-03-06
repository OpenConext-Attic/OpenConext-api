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

/**
 * {@link Test} for {@link TabOverviewController}
 *
 */
public class TabOverviewControllerTest extends AbstractControllerTest {

  /**
   * Test method for {@link nl.surfnet.coin.portal.control.TabOverviewController#taboverview(org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)}.
   * @throws Exception 
   */
  @Test
  public void testTaboverview() throws Exception {
    TabOverviewController controller = new TabOverviewController();
    autoWireRemainingResources(controller);
    
    String view = controller.taboverview(getModelMap(), getRequest());
    assertEquals(view,"taboverview");
  }

}
