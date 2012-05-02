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

import nl.surfnet.coin.api.oauth.ClientMetaData;
import nl.surfnet.coin.api.oauth.ExtendedBaseClientDetails;
import nl.surfnet.coin.api.oauth.ExtendedBaseConsumerDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.OAuthAuthenticationDetails;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class AbstractApiController {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractApiController.class);

  /**
   * Get the username of the (via oauth) authenticated user that performs this
   * request.
   * 
   * @return the username in case of an end user authorized request (3 legged
   *         oauth1, authorization code grant oauth2) or the consumer key in
   *         case of unauthorized requests.
   */
  protected String getOnBehalfOf() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      return null;
    } else {
      if (auth.getPrincipal() instanceof ConsumerDetails) {
        // Two legged, it does not have end user details
        return null;
      } else if (auth.getPrincipal() instanceof String) {
        return (String) auth.getPrincipal();
      } else if (auth.getPrincipal() instanceof OAuthAuthenticationDetails) {
        return ((OAuthAuthenticationDetails) auth.getPrincipal()).getConsumerDetails().getConsumerName();
      } else {
        return ((UserDetails) auth.getPrincipal()).getUsername();
      }
    }
  }

  protected ClientMetaData getClientMetaData() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ClientMetaData clientMetaData;
    if (principal instanceof ExtendedBaseClientDetails) {
      ExtendedBaseClientDetails clientDetails = (ExtendedBaseClientDetails) principal;
      clientMetaData = clientDetails.getClientMetaData();
    } else if (principal instanceof ExtendedBaseConsumerDetails) {
      ExtendedBaseConsumerDetails consumerDetails = (ExtendedBaseConsumerDetails) principal;
      clientMetaData = consumerDetails.getClientMetaData();
    } else {
      throw new RuntimeException(
          "The Principal from 'SecurityContextHolder.getContext().getAuthentication().getPrincipal()' is an unknown instance("
              + (principal != null ? principal.getClass() : "null") + ")");
    }
    Assert.notNull(clientMetaData, "ClientMetaData may not be null for checking ACL's");
    return clientMetaData;
  }

  /**
   * Handle CORS preflight request.
   * 
   * @param origin
   *          the Origin header
   * @param methods
   *          the "Access-Control-Request-Method" header
   * @param headers
   *          the "Access-Control-Request-Headers" header
   * @return a ResponseEntity with 204 (no content) and the right response
   *         headers
   */
  @RequestMapping(method = RequestMethod.OPTIONS, value = "/**")
  public ResponseEntity<String> preflightCORS(@RequestHeader("Origin")
  String origin, @RequestHeader(value = "Access-Control-Request-Method", required = false)
  String[] methods, @RequestHeader(value = "Access-Control-Request-Headers", required = false)
  String[] headers) {
    LOG.debug("Hitting CORS preflight handler. Origin header: {}, methods: {}, headers: {}", new Object[] { origin,
        methods, headers });

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Allow", "GET,OPTIONS,HEAD");
    responseHeaders.set("Access-Control-Allow-Methods", "GET,OPTIONS,HEAD");
    responseHeaders.set("Access-Control-Allow-Headers", "Authorization");
    responseHeaders.set("Access-Control-Max-Age", "86400"); // allow cache of 1
                                                            // day
    return new ResponseEntity<String>(null, responseHeaders, HttpStatus.OK);
  }
}
