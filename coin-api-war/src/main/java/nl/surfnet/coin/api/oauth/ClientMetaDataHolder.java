/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nl.surfnet.coin.api.oauth;

import nl.surfnet.coin.api.shib.ShibbolethAuthenticationToken;

/**
 * Holds a reference to the ClientMetaData to be accessed in different parts of
 * the flow. Necessary to persist the ClientMetaData in the
 * {@link ShibbolethAuthenticationToken}
 * 
 */
public class ClientMetaDataHolder {

  private static final ThreadLocal<ClientMetaData> clientMetaDataContext = new ThreadLocal<ClientMetaData>();
  public static final String CLIENT_META_DATA_KEY = "CLIENT_META_DATA_KEY"; 
  
  public static void storeClientMetaData(ShibbolethAuthenticationToken token) {
    ClientMetaData clientMetaData = clientMetaDataContext.get();
    if (clientMetaData != null) {
      token.setClientMetaData(clientMetaData);
      clientMetaDataContext.set(null);
    }
  }
  
  public static void setClientMetaData(ClientMetaData clientMetaData) {
    clientMetaDataContext.set(clientMetaData);
  }

  public static ClientMetaData getClientMetaData() {
    return clientMetaDataContext.get();
  }

  public static void storeClientMetaData(ClientMetaDataUser principal) {
    ClientMetaData clientMetaData = clientMetaDataContext.get();
    if (clientMetaData != null) {
      principal.setClientMetaData(clientMetaData);
      clientMetaDataContext.set(null);
    }
    
  }
}
