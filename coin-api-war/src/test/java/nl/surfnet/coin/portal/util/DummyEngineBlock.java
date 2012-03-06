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

package nl.surfnet.coin.portal.util;

import nl.surfnet.coin.mock.MockHandler;
import nl.surfnet.coin.mock.MockHtppServer;
import org.mortbay.jetty.Server;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Dummy engine block for mocking engine block Person and Group services
 * 
 */
public class DummyEngineBlock {

  public void mockEngineBlock() {
    final ClassPathResource teams = new ClassPathResource(
        "jsons/mock-multiple-teams.json");
    final ClassPathResource persons = new ClassPathResource(
        "jsons/mock-multiple-persons.json");
    final ClassPathResource person = new ClassPathResource(
        "jsons/mock-single-person-extensions.json");
    final ClassPathResource idps = new ClassPathResource("idp-metadata.xml");
    final ClassPathResource oauth = new ClassPathResource(
        "jsons/mock-oauth.json");
    final ClassPathResource oauthLegged = new ClassPathResource(
        "jsons/mock-oauth-three-legged-pix.json");
    final ClassPathResource calendarTeams = new ClassPathResource(
        "jsons/mock-calendar-teams.json");
    final ClassPathResource calendarPersons = new ClassPathResource(
        "jsons/mock-calendar-persons.json");
    final ClassPathResource serviceProviders = new ClassPathResource(
        "jsons/mock-service-providers.json");
    new MockHtppServer(8082) {
      protected MockHandler createHandler(Server server) {
        return new MockHandler(server) {
          public void handle(String target, HttpServletRequest request,
              HttpServletResponse response, int dispatch) throws IOException,
              ServletException {

            String requestURI = request.getRequestURI();
            if (requestURI.startsWith("/social/rest/groups")) {
              setResponseResource(calendarTeams);
            } else if (requestURI.startsWith("/social/rest/people")) {
              setResponseResource(calendarPersons);
            } else if (requestURI.startsWith("/rest/groups")
                    || requestURI.startsWith("/social/groups")) {
              setResponseResource(teams);
            } else if (requestURI.startsWith("/authentication/proxy")) {
              setResponseResource(idps);
            } else if (requestURI.startsWith("/service/metadata")) {
              if (request.getQueryString().contains("consumer_key")) {
                setResponseResource(oauthLegged);
              } else {
                setResponseResource(oauth);
              }
            } else if (requestURI.endsWith("service/sp?all")) {
              setResponseResource(serviceProviders);
            } else {
              int length = requestURI.split("/").length;
              boolean multiple = length > 4;
              if (multiple) {
                setResponseResource(persons);
              } else {
                setResponseResource(person);
              }
            }
            super.handle(target, request, response, dispatch);
          }
        };
      }
    }.startServerSync();
  }

  public static void main(String[] args) {
    DummyEngineBlock engineBlock = new DummyEngineBlock();
    System.out
        .println("**** Starting dummy engineblock. Will run forever. ****");
    engineBlock.mockEngineBlock();
  }
}
