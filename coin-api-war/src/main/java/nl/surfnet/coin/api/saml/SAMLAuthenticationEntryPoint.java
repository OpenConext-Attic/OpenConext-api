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
package nl.surfnet.coin.api.saml;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.RequesterID;
import org.opensaml.saml2.core.Scoping;
import org.opensaml.saml2.core.impl.RequesterIDBuilder;
import org.opensaml.saml2.core.impl.ScopingBuilder;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.security.*;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.criteria.UsageCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import nl.surfnet.coin.api.oauth.OpenConextClientDetails;
import nl.surfnet.coin.api.oauth.OpenConextConsumerDetails;
import nl.surfnet.coin.api.oauth.OpenConextOauth1TokenServices;
import nl.surfnet.coin.api.service.OpenConextClientDetailsService;
import nl.surfnet.spring.security.opensaml.AuthnRequestGenerator;
import nl.surfnet.spring.security.opensaml.util.IDService;
import nl.surfnet.spring.security.opensaml.util.TimeService;
import nl.surfnet.spring.security.opensaml.xml.EndpointGenerator;

/**
 * SamlAuthenticationEntryPoint.java
 *
 */
public class SAMLAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private static final Logger LOG = LoggerFactory.getLogger(SAMLAuthenticationEntryPoint.class);

  private TimeService timeService = new TimeService();
  private IDService idService = new IDService();

  @Resource(name="openSAMLContext")
  private OpenSAMLContext openSAMLContext;
  private final ScopingBuilder scopingBuilder = new ScopingBuilder();
  private final RequesterIDBuilder requesterIDBuilder = new RequesterIDBuilder();

  @Resource(name="janusClientDetailsService")
  private OpenConextClientDetailsService clientDetailsService;

  @Resource
  private OpenConextOauth1TokenServices oauth1TokenServices;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException,
      ServletException {
    sendAuthnRequest(request, response);
  }

  private void sendAuthnRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
    AuthnRequestGenerator authnRequestGenerator = new AuthnRequestGenerator(openSAMLContext.entityId(), timeService,
        idService);
    EndpointGenerator endpointGenerator = new EndpointGenerator();

    final String target = openSAMLContext.idpUrl();

    Endpoint endpoint = endpointGenerator.generateEndpoint(
        SingleSignOnService.DEFAULT_ELEMENT_NAME, target, openSAMLContext.assertionConsumerUri());

    AuthnRequest authnRequest = authnRequestGenerator.generateAuthnRequest(target, openSAMLContext.assertionConsumerUri());

    Scoping scoping = scopingBuilder.buildObject();

    scoping.getRequesterIDs().add(createRequesterID(getSPEntityIdByRequest(request)));
    authnRequest.setScoping(scoping);
    try {
      CriteriaSet criteriaSet = new CriteriaSet();
      criteriaSet.add(new EntityIDCriteria(openSAMLContext.entityId()));
      criteriaSet.add(new UsageCriteria(UsageType.SIGNING));

      Credential signingCredential = openSAMLContext.keyStoreCredentialResolver().resolveSingle(criteriaSet);

      Validate.notNull(signingCredential);

      String originalUrl = String.format("%s?%s", request.getRequestURI(), request.getQueryString());
      openSAMLContext.samlMessageHandler().sendSAMLMessage(authnRequest, endpoint, response, originalUrl, signingCredential);
    } catch (MessageEncodingException mee) {
      LOG.error("Could not send authnRequest to Identity Provider.", mee);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } catch (org.opensaml.xml.security.SecurityException e) {
      LOG.error("Could not send authnRequest to Identity Provider.", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get the SP Entity ID from the OAuth client id in the HTTP request
   */
  public String getSPEntityIdByRequest(HttpServletRequest request) {
    if (request.getParameter("client_id") != null) {
      // oauth2
      String clientId = request.getParameter("client_id");
      OpenConextClientDetails clientDetails = (OpenConextClientDetails) clientDetailsService.loadClientByClientId(clientId);
      return clientDetails.getClientMetaData().getAppEntityId();
    } else {
      // oauth 1
      // First get the consumer key by the token.
      String token = request.getParameter("oauth_token");
      String clientId = oauth1TokenServices.getToken(token).getConsumerKey();
      // Then get the SP entity ID from Janus
      OpenConextConsumerDetails consumerDetails = (OpenConextConsumerDetails) clientDetailsService.loadConsumerByConsumerKey(clientId);
      return consumerDetails.getClientMetaData().getAppEntityId();
    }
  }

  /**
   * Simple builder
   */
  public RequesterID createRequesterID(String id) {
    RequesterID requesterID = requesterIDBuilder.buildObject();
    requesterID.setRequesterID(id);
    return requesterID;
  }
}
