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

package nl.surfnet.coin.janus;

import java.util.Map;

/**
 * Interface to Janus.
 */
public interface Janus {



  public enum Metadata {

    OAUTH_SECRET("coin:oauth:secret"),
    OAUTH_CONSUMERSECRET("coin:oauth:consumer_secret");

    private String val;

    public String val() {
      return val;
    }

    Metadata(String val) {
      this.val = val;
    }
  }
  /**
   * Get a client's metadata by his client_id.
   * @param clientId the client_id
   * @return the secret
   */
  Map<String, String> getMetadataByClientId(String clientId);
}
