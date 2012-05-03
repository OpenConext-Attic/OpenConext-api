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
import nl.surfnet.coin.api.oauth.ClientMetaDataPrincipal;
import nl.surfnet.coin.api.oauth.ExtendedBaseClientDetails;
import nl.surfnet.coin.api.oauth.ExtendedBaseConsumerDetails;
import nl.surfnet.coin.api.shib.ShibbolethAuthenticationToken;

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
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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
      Object principal = auth.getPrincipal();
      if (principal instanceof ConsumerDetails) {
        // Two legged, it does not have end user details
        return null;
      } else if (principal instanceof String) {
        return (String) principal;
      } else if (principal instanceof OAuthAuthenticationDetails) {
        return ((OAuthAuthenticationDetails) principal).getConsumerDetails().getConsumerName();
      } else if (principal instanceof ClientMetaDataPrincipal) {
        return ((ClientMetaDataPrincipal) principal).getRemoteUser();
      } else {
        return ((UserDetails) principal).getUsername();
      }
    }
  }

  protected ClientMetaData getClientMetaData() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    ClientMetaData metaData = null;
    if (authentication instanceof OAuth2Authentication) {
      OAuth2Authentication oauth2 = (OAuth2Authentication) authentication;
      Authentication userAuthentication = oauth2.getUserAuthentication();
      if (userAuthentication instanceof ShibbolethAuthenticationToken) {
        ShibbolethAuthenticationToken shib = (ShibbolethAuthenticationToken) userAuthentication;
        metaData = shib.getClientMetaData();
      }

    }
    Assert.notNull(metaData, "ClientMetaData may not be null for checking ACL's");
    return metaData;
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
