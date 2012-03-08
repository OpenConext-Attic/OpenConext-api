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
package nl.surfnet.coin.api.client;

import nl.surfnet.coin.api.client.domain.Person;

import java.util.List;

/**
 * Implementation of 
 * 
 */
public class OpenConextOAuthClientImpl implements OpenConextOAuthClient{


    @Override
    public Person getPerson(String userId, String onBehalfOf) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Person> getPeople(String groupId, String onBehalfOf) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
