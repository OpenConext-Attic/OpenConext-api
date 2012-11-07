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
import java.util.Arrays;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.app.VelocityEngine;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.common.binding.security.IssueInstantRule;
import org.opensaml.common.binding.security.MessageReplayRule;
import org.opensaml.saml2.binding.decoding.HTTPPostSimpleSignDecoder;
import org.opensaml.saml2.core.Response;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.util.storage.MapBasedStorageService;
import org.opensaml.util.storage.ReplayCache;
import org.opensaml.util.storage.ReplayCacheEntry;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.security.SecurityPolicyResolver;
import org.opensaml.ws.security.provider.StaticSecurityPolicyResolver;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.validation.ValidationException;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import nl.surfnet.spring.security.opensaml.AssertionConsumer;
import nl.surfnet.spring.security.opensaml.AssertionConsumerImpl;
import nl.surfnet.spring.security.opensaml.KeyStore;
import nl.surfnet.spring.security.opensaml.Provisioner;
import nl.surfnet.spring.security.opensaml.SAMLMessageHandler;
import nl.surfnet.spring.security.opensaml.SAMLMessageHandlerImpl;
import nl.surfnet.spring.security.opensaml.SecurityPolicyDelegate;
import nl.surfnet.spring.security.opensaml.ServiceProviderAuthenticationException;
import nl.surfnet.spring.security.opensaml.SignatureSecurityPolicyRule;
import nl.surfnet.spring.security.opensaml.crypt.KeyStoreCredentialResolverDelegate;
import nl.surfnet.spring.security.opensaml.xml.SAML2ValidatorSuite;

/**
 * Context that wires the required OpenSAML configuration. All methods can be
 * overridden to customize behavior.
 */
@Component(value = "openSAMLContext")
public class OpenSAMLContext {

  private static final String DEFAULT_ASSERTION_CONSUMER_URI = "/assertionConsumerService";

  private long replayCacheDuration;

  private int maxParserPoolSize;

  private String entityId;

  private int clockSkew;

  private int newExpires;

  private String assertionConsumerURI;

  private String idpEntityId;

  private String idpCertificate;


  private Provisioner provisioner;
  private SAMLMessageHandlerImpl samlMessageHandler;
  private final SAML2ValidatorSuite validatorSuite;

  private String idpUrl;
  private String spPrivateKey;
  private String spCertificate;

  public OpenSAMLContext() throws IOException {
    final Properties properties = PropertiesLoaderUtils.loadAllProperties("coin-api.properties");
    // Bootstrap openSAML
    try {
      DefaultBootstrap.bootstrap();
    } catch (ConfigurationException e) {
      throw new RuntimeException(e);
    }

    replayCacheDuration = Long.parseLong(properties.getProperty("replayCacheDuration", "14400000"));
    maxParserPoolSize = Integer.parseInt(properties.getProperty("maxParserPoolSize", "2"));
    entityId = properties.getProperty("entityId", "no-property-named-entityId");
    clockSkew = Integer.parseInt(properties.getProperty("clockSkew", "90"));
    newExpires = Integer.parseInt(properties.getProperty("newExpires", "300"));
    assertionConsumerURI = properties.getProperty("assertionConsumerURI", DEFAULT_ASSERTION_CONSUMER_URI);

    idpEntityId = properties.getProperty("idpEntityId", "no-property-named-idpEntityId");
    idpCertificate = properties.getProperty("idpCertificate", "no-property-named-idpCertificate");
    idpUrl = properties.getProperty("idpUrl", "no-property-named-idpUrl");
    spPrivateKey = properties.getProperty("spPrivateKey", "no-property-named-spPrivateKey");
    spCertificate = properties.getProperty("spCertificate", "no-property-named-spCertificate");

    String uuidAttribute = properties.getProperty("saml-uuid-attribute", "urn:oid:1.3.6.1.4.1.1076.20.40.40.1");
    
    this.provisioner = new SAMLProvisioner(uuidAttribute);

    samlMessageHandler = new SAMLMessageHandlerImpl(samlMessageDecoder(), securityPolicyResolver());
    samlMessageHandler.setEntityId(entityId);
    samlMessageHandler.setVelocityEngine(velocityEngine());
    samlMessageHandler.setNeedsSigning(true);
    validatorSuite = new SAML2ValidatorSuite();

  }

  protected VelocityEngine velocityEngine() {

    final VelocityEngineFactoryBean velocityEngineFactoryBean = new VelocityEngineFactoryBean();
    velocityEngineFactoryBean.setPreferFileSystemAccess(false);
    Properties velocityEngineProperties = new Properties();
    velocityEngineProperties.setProperty("resource.loader", "classpath");
    velocityEngineProperties.setProperty("classpath.resource.loader.class",
        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    velocityEngineFactoryBean.setVelocityProperties(velocityEngineProperties);

    try {
      return velocityEngineFactoryBean.createVelocityEngine();
    } catch (IOException e) {
      throw new RuntimeException("Unable to create velocity engine instance");
    }
  }

  public String assertionConsumerUri() {
    return assertionConsumerURI;
  }

  protected ReplayCache replayCache() {
    return new ReplayCache(new MapBasedStorageService<String, ReplayCacheEntry>(), replayCacheDuration);
  }

  protected MessageReplayRule messageReplayRule() {
    return new MessageReplayRule(replayCache());
  }

  protected IssueInstantRule issueInstantRule() {
    return new IssueInstantRule(clockSkew, newExpires);
  }

  protected CredentialResolver keyStoreCredentialResolver() {
    final KeyStoreCredentialResolverDelegate keyStoreCredentialResolverDelegate = new KeyStoreCredentialResolverDelegate();
    keyStoreCredentialResolverDelegate.setKeyStore(keyStore());
    return keyStoreCredentialResolverDelegate;
  }

  protected SignatureSecurityPolicyRule signatureBuilder() {
    final SignatureSecurityPolicyRule signatureSecurityPolicyRule = new SignatureSecurityPolicyRule(new SAMLSignatureProfileValidator());
    signatureSecurityPolicyRule.setCredentialResolver(keyStoreCredentialResolver());
    try {
      signatureSecurityPolicyRule.afterPropertiesSet();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return signatureSecurityPolicyRule;
  }

  protected SecurityPolicyDelegate securityPolicy() {
    return new SecurityPolicyDelegate(Arrays.asList(signatureBuilder(), issueInstantRule(), messageReplayRule()));
  }

  protected SecurityPolicyResolver securityPolicyResolver() {
    return new StaticSecurityPolicyResolver(securityPolicy());
  }

  protected SAMLMessageDecoder samlMessageDecoder() {
    final BasicParserPool basicParserPool = new BasicParserPool();
    basicParserPool.setMaxPoolSize(maxParserPoolSize);

    return new HTTPPostSimpleSignDecoder(basicParserPool);
  }

  public SAMLMessageHandler samlMessageHandler() {
    return this.samlMessageHandler;
  }

  public AssertionConsumer assertionConsumer() {
    final AssertionConsumerImpl assertionConsumer = new AssertionConsumerImpl();
    assertionConsumer.setProvisioner(provisioner);
    return assertionConsumer;
  }

  protected KeyStore keyStore() {
    final KeyStore keyStore = new KeyStore();

    keyStore.addCertificate(idpEntityId, idpCertificate);
    keyStore.addPrivateKey(entityId(), spPrivateKey, spCertificate, "somepass");

    return keyStore;
  }

  public String entityId() {
    return entityId;
  }

  public SAML2ValidatorSuite validatorSuite() {
    return validatorSuite;
  }

  protected UserDetails authenticate(Response samlResponse) {
    return assertionConsumer().consume(samlResponse);
  }

  protected boolean isSAMLResponse(HttpServletRequest request) {
    return request.getParameter("SAMLResponse") != null;
  }

  protected Response extractSamlResponse(HttpServletRequest request)  {

    SAMLMessageContext messageContext;

    try {
      messageContext = samlMessageHandler.extractSAMLMessageContext(request);
    } catch (MessageDecodingException me) {
      throw new ServiceProviderAuthenticationException("Could not decode SAML Response", me);
    } catch (org.opensaml.xml.security.SecurityException se) {
      throw new ServiceProviderAuthenticationException("Could not decode SAML Response", se);
    }

    if (!(messageContext.getInboundSAMLMessage() instanceof Response)) {
      throw new ServiceProviderAuthenticationException("SAML Message was not a Response.");
    }

    final Response inboundSAMLMessage = (Response) messageContext.getInboundSAMLMessage();

    try {
      validatorSuite().validate(inboundSAMLMessage);
    } catch (ValidationException e) {
      throw new ServiceProviderAuthenticationException("Could not validate SAML Response", e);
    }
    return inboundSAMLMessage;
  }

  public String idpUrl() {
    return idpUrl;
  }
}
