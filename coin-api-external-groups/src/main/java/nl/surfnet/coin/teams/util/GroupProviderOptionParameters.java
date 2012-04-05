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

package nl.surfnet.coin.teams.util;

/**
 * Constants for allowed options key of a {@link nl.surfnet.coin.teams.domain.GroupProvider}
 */
public final class GroupProviderOptionParameters {
  private GroupProviderOptionParameters() {
  }

  public static final String ADAPTER = "adapter";
  public static final String URL = "url";
  public static final String PROTOCOL = "protocol";
  public static final String HOST = "host";
  public static final String VERSION = "version";
  public static final String PATH = "path";
  public static final String USERNAME = "user";
  public static final String PASSWORD = "password";
  public static final String CONSUMER_KEY = "auth.consumerKey";
  public static final String CONSUMER_SECRET = "auth.consumerSecret";
  public static final String SIGNATURE_METHOD = "auth.signatureMethod";
  public static final String CALLBACK_URL = "auth.callbackUrl";
  public static final String SITE_URL = "auth.siteUrl";
  public static final String REQUEST_TOKEN_URL = "auth.requestTokenUrl";
  public static final String ACCESS_TOKEN_URL = "auth.accessTokenUrl";
  public static final String AUTHORIZE_URL = "auth.authorizeUrl";
  public static final String USER_AUTHORIZATION_URL = "auth.userAuthorizationUrl";
  public static final String REQUEST_METHOD = "auth.requestMethod";
  public static final String RSA_PUBLIC_KEY = "auth.rsaPublicKey";
  public static final String RSA_PRIVATE_KEY = "auth.rsaPrivateKey";
  public static final String REQUEST_SCHEME = "auth.requestScheme";
  public static final String TIMEOUT = "timeout";
  public static final String SSL_VERIFY_HOST = "ssl_verifyhost";
  public static final String SSL_VERIFY_PEER = "ssl_verifypeer";
}
