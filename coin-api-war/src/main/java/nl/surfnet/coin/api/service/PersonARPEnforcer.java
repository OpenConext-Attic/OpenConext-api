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

package nl.surfnet.coin.api.service;

import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.janus.domain.ARP;

public class PersonARPEnforcer {
  /**
   * Mangle a given Person using the given ARP.
   * @param person Person to mangle
   * @param arp ARP to use
   * @return the given person, mangled
   */
  public Person enforceARP(Person person, ARP arp) {
    return person;
  }
}
