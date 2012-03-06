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

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.surfnet.coin.mock.MockHandler;
import nl.surfnet.coin.mock.MockHtppServer;

import org.mortbay.jetty.Server;
import org.springframework.core.io.ByteArrayResource;

/**
 * Dummy endpoint for mocking makeRequest
 * 
 */
public class DummyEndPoint {

  public void mockEngineBlock() {
    new MockHtppServer(8083) {
      protected MockHandler createHandler(Server server) {
        return new MockHandler(server) {
          public void handle(String target, HttpServletRequest request,
              HttpServletResponse response, int dispatch) throws IOException,
              ServletException {
            response.addHeader("Content-Type", "text/html");// "application/json");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            String idp = request.getParameter("identityProviderEntityId");
            String sp = request.getParameter("serviceProviderEntityId");
            idp = URLDecoder.decode(idp);
            sp = URLDecoder.decode(sp);
            setResponseResource(new ByteArrayResource(
               // "{\"returnUrl\": \"http://www.google.nl\", \"licenseStatus\": \"LICENSE_OK\"}"
                "{\"licenseStatus\": \"testje\"}"
                             .getBytes()));
            super.handle(target, request, response, dispatch);
          }
        };
      }
    }.startServerSync();
  }

  public static void main(String[] args) {
    DummyEndPoint engineBlock = new DummyEndPoint();
    System.out.println("**** Starting dummy endpoint. Will run forever. ****");
    engineBlock.mockEngineBlock();
  }
}
