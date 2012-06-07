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

import java.util.Set;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * A {@link DefaultRedirectResolver} that throws an OAuth exception when the an
 * implicit grant request is made without a redirectUri in janus (see
 * https://jira.surfconext.nl/jira/browse/BACKLOG-511).
 * 
 */
public class ImplicitGrantExplicitRedirectResolver extends DefaultRedirectResolver {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver
   * #resolveRedirect(java.lang.String,
   * org.springframework.security.oauth2.provider.ClientDetails)
   */
  @Override
  public String resolveRedirect(String requestedRedirect, ClientDetails client) throws OAuth2Exception {
    Set<String> redirectUris = client.getRegisteredRedirectUri();

    boolean implicitGrant = isImplicitGrant();

    if ((redirectUris == null || redirectUris.isEmpty()) && implicitGrant) {
      throw new OAuth2Exception("A redirect_uri must be configured for implicit grant.");
    }
    return super.resolveRedirect(requestedRedirect, client);
  }

  /*
   * Bit of a hack, but the current interface does not allow us to determine the response type (which we can derive from the query parameter map)
   */
  private boolean isImplicitGrant() {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    String responseType = (String) requestAttributes.getRequest().getParameter("response_type");
    Set<String> responseTypes = OAuth2Utils.parseParameterList(responseType);
    return responseTypes.contains("token");
  }
}
