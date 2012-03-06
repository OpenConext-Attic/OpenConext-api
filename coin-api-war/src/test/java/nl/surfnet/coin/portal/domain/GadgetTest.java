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


package nl.surfnet.coin.portal.domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GadgetTest {
  
  private Gadget gadget;
  
  @Before
  public void setup() {
    gadget = new Gadget();
  }
  
  @Test
  public void testSetTeamNormal() {
    gadget.setTeam("coin");
    assertEquals("groupContext=coin", gadget.getPrefs());
  }
  
  @Test
  public void testSetTeamEmpty() {
    gadget.setTeam("");
    assertEquals("groupContext=", gadget.getPrefs());
  }
  
  @Test
  public void testSetTeamToNull() {
    gadget.setTeam(null);
    assertEquals("groupContext=", gadget.getPrefs());
  }
  
  @Test
  public void testSetTeamWhenItAlreadyHasATeam() {
    gadget.setPrefs("groupContext=coin");
    gadget.setTeam("coin2");
    assertEquals("groupContext=coin2", gadget.getPrefs());
  }
  
  @Test
  public void testSetTeamWhenItAlreadyHasAnEmptyTeam() {
    gadget.setPrefs("groupContext=");
    gadget.setTeam("coin2");
    assertEquals("groupContext=coin2", gadget.getPrefs());
  }
  
  @Test
  public void testSetTeamWhenThereAreAlreadyPrefs() {
    gadget.setPrefs("test1=1&groupContext=coin&test2=2");
    gadget.setTeam("coin2");
    assertEquals("test1=1&groupContext=coin2&test2=2", gadget.getPrefs());
  }
  
  // TODO test with team names that contain "=" or "&" or " "
}
