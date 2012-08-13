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
public interface GroupProviderOptionParameters {

  String ADAPTER = "adapter";
  String URL = "url";
  String PROTOCOL = "protocol";
  String HOST = "host";
  String VERSION = "version";
  String PATH = "path";
  String USERNAME = "user";
  String PASSWORD = "password";
  String CONSUMER_KEY = "auth.consumerKey";
  String CONSUMER_SECRET = "auth.consumerSecret";
  String SIGNATURE_METHOD = "auth.signatureMethod";
  String CALLBACK_URL = "auth.callbackUrl";
  String SITE_URL = "auth.siteUrl";
  String REQUEST_TOKEN_URL = "auth.requestTokenUrl";
  String ACCESS_TOKEN_URL = "auth.accessTokenUrl";
  String AUTHORIZE_URL = "auth.authorizeUrl";
  String USER_AUTHORIZATION_URL = "auth.userAuthorizationUrl";
  String REQUEST_METHOD = "auth.requestMethod";
  String RSA_PUBLIC_KEY = "auth.rsaPublicKey";
  String RSA_PRIVATE_KEY = "auth.rsaPrivateKey";
  String REQUEST_SCHEME = "auth.requestScheme";
  String TIMEOUT = "timeout";
  String SSL_VERIFY_HOST = "ssl_verifyhost";
  String SSL_VERIFY_PEER = "ssl_verifypeer";
}
