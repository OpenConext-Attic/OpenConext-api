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

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import nl.surfnet.coin.janus.Janus.Metadata;

/**
 * Metadata from janus about the SP (which in OAuth1 terms is a consumer and in
 * OAuth2 terms a client)
 * 
 */
//@SuppressWarnings("serial")
public class ClientMetaData implements Serializable {

  private static final long serialVersionUID = 1L;
  private String appTitle;
  private String appIcon;
  private String appDescription;
  private String appThumbNail;
  private String appEntityId;

  public static ClientMetaData fromMetaData(Map<String, String> metaData) {
    ClientMetaData clientMetaData = new ClientMetaData();
    clientMetaData.setAppTitle(metaData.get(Metadata.OAUTH_APPTITLE.val()));
    clientMetaData.setAppIcon(metaData.get(Metadata.OAUTH_APPICON.val()));
    clientMetaData.setAppDescription(metaData.get(Metadata.OAUTH_APPDESCRIPTION.val()));
    clientMetaData.setAppThumbNail(metaData.get(Metadata.OAUTH_APPTHUMBNAIL.val()));
    clientMetaData.setAppEntityId(metaData.get(Metadata.ENTITY_ID.val()));
    return clientMetaData;
  }

  /**
   * @return the appTitle
   */
  public String getAppTitle() {
    return appTitle;
  }

  /**
   * @param appTitle the appTitle to set
   */
  public void setAppTitle(String appTitle) {
    this.appTitle = appTitle;
  }

  /**
   * @return the appIcon
   */
  public String getAppIcon() {
    return appIcon;
  }

  /**
   * @param appIcon the appIcon to set
   */
  public void setAppIcon(String appIcon) {
    this.appIcon = appIcon;
  }

  /**
   * @return the appDescription
   */
  public String getAppDescription() {
    return appDescription;
  }

  /**
   * @param appDescription the appDescription to set
   */
  public void setAppDescription(String appDescription) {
    this.appDescription = appDescription;
  }

  /**
   * @return the appThumbNail
   */
  public String getAppThumbNail() {
    return appThumbNail;
  }

  /**
   * @param appThumbNail the appThumbNail to set
   */
  public void setAppThumbNail(String appThumbNail) {
    this.appThumbNail = appThumbNail;
  }

  /**
   * @return the appEntityId
   */
  public String getAppEntityId() {
    return appEntityId;
  }

  /**
   * @param appEntityId the appEntityId to set
   */
  public void setAppEntityId(String appEntityId) {
    this.appEntityId = appEntityId;
  }

  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
  }
}
