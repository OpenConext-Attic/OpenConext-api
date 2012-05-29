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

package nl.surfnet.coin.api.oauth;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import nl.surfnet.coin.janus.domain.EntityMetadata;

/**
 * Metadata from janus about the SP (which in OAuth1 terms is a consumer and in
 * OAuth2 terms a client)
 *
 */
public class JanusClientMetadata implements ClientMetaData, Serializable {

  private final static long serialVersionUid = 1L;

  private EntityMetadata metadata;

  public JanusClientMetadata() {
  }

  public JanusClientMetadata(EntityMetadata metadata) {
    this.metadata = metadata;
  }


  @Override
  public String getAppTitle() {
    return metadata.getAppTitle();
  }


  @Override
  public String getAppIcon() {
    return metadata.getAppIcon();
  }


  @Override
  public String getAppDescription() {
    return metadata.getAppDescription();
  }


  @Override
  public String getAppThumbNail() {
    return metadata.getAppThumbNail();
  }


  @Override
  public String getAppEntityId() {
    return metadata.getAppEntityId();
  }

  @Override
  public String getConsumerKey() {
    return metadata.getOauthConsumerKey();
  }


  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(metadata).toString();
  }
}
