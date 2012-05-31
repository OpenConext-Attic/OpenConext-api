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

package nl.surfnet.coin.janus.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.list.UnmodifiableList;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import nl.surfnet.coin.janus.Janus;

/**
 * Representation of an entity's metadata in Janus.
 */
public class EntityMetadata implements Serializable {

  private static final long serialVersionUID = 1L;

  private String oauthConsumerKey;
  private String oauthConsumerSecret;
  private String appTitle;
  private String appIcon;
  private String appDescription;
  private String appThumbNail;
  private String appEntityId;
  private String oauthCallbackUrl;
  private boolean twoLeggedOauthAllowed;
  private String appLogoUrl;
  private String appHomeUrl;
  private String eula;
  private List<Contact> contacts = Collections.synchronizedList(new ArrayList<Contact>());

  private boolean isIdpVisibleOnly;

  public static EntityMetadata fromMetadataMap(Map<String, Object> metadata) {
    EntityMetadata em = new EntityMetadata();

    em.setOauthConsumerSecret((String) metadata.get(Janus.Metadata.OAUTH_SECRET.val()));
    em.setOauthConsumerKey((String) metadata.get(Janus.Metadata.OAUTH_CONSUMERKEY.val()));
    em.setAppDescription((String) metadata.get(Janus.Metadata.OAUTH_APPDESCRIPTION.val()));
    em.setAppIcon((String) metadata.get(Janus.Metadata.OAUTH_APPICON.val()));
    em.setAppThumbNail((String) metadata.get(Janus.Metadata.OAUTH_APPTHUMBNAIL.val()));
    em.setAppTitle((String) metadata.get(Janus.Metadata.OAUTH_APPTITLE.val()));
    em.setOauthCallbackUrl((String) metadata.get(Janus.Metadata.OAUTH_CALLBACKURL.val()));

    em.setAppHomeUrl((String) metadata.get(Janus.Metadata.ORGANIZATION_URL.val()));
    em.setAppLogoUrl((String) metadata.get(Janus.Metadata.LOGO_URL.val()));
    em.setEula((String) metadata.get(Janus.Metadata.EULA.val()));

    em.setTwoLeggedOauthAllowed(false);
    if (metadata.get(Janus.Metadata.OAUTH_TWOLEGGEDALLOWED.val()) != null) {
      em.setTwoLeggedOauthAllowed((Boolean) metadata.get(Janus.Metadata.OAUTH_TWOLEGGEDALLOWED.val()));
    }

    em.setIdpVisibleOnly(false);
    if (metadata.get(Janus.Metadata.SS_IDP_VISIBLE_ONLY.val()) != null) {
      em.setIdpVisibleOnly((Boolean) metadata.get(Janus.Metadata.SS_IDP_VISIBLE_ONLY.val()));
    }

    final Object c0Mail = metadata.get(Janus.Metadata.CONTACTS_0_EMAIL.val());
    if (metadata.get(Janus.Metadata.CONTACTS_0_TYPE.val()) != null &&
        !emptyString(c0Mail)) {
      Contact contact = new Contact();
      contact.setEmailAddress((String) c0Mail);
      contact.setGivenName((String) metadata.get(Janus.Metadata.CONTACTS_0_GIVENNAME.val()));
      contact.setSurName((String) metadata.get(Janus.Metadata.CONTACTS_0_SURNAME.val()));
      Object phone = metadata.get(Janus.Metadata.CONTACTS_0_TELEPHONE.val());
      contact.setTelephoneNumber(getPhoneAsString(phone));
      contact.setType(Contact.Type.valueOf((String) metadata.get(Janus.Metadata.CONTACTS_0_TYPE.val())));
      em.addContact(contact);
    }

    final Object c1Mail = metadata.get(Janus.Metadata.CONTACTS_1_EMAIL.val());
    if (metadata.get(Janus.Metadata.CONTACTS_1_TYPE.val()) != null &&
        !emptyString(c1Mail)) {
      Contact contact = new Contact();
      contact.setEmailAddress((String) c1Mail);
      contact.setGivenName((String) metadata.get(Janus.Metadata.CONTACTS_1_GIVENNAME.val()));
      contact.setSurName((String) metadata.get(Janus.Metadata.CONTACTS_1_SURNAME.val()));
      Object phone = metadata.get(Janus.Metadata.CONTACTS_1_TELEPHONE.val());
      contact.setTelephoneNumber(getPhoneAsString(phone));
      contact.setType(Contact.Type.valueOf((String) metadata.get(Janus.Metadata.CONTACTS_1_TYPE.val())));
      em.addContact(contact);
    }

    final Object c2Mail = metadata.get(Janus.Metadata.CONTACTS_2_EMAIL.val());
    if (metadata.get(Janus.Metadata.CONTACTS_2_TYPE.val()) != null &&
        !emptyString(c2Mail)) {
      Contact contact = new Contact();
      contact.setEmailAddress((String) c2Mail);
      contact.setGivenName((String) metadata.get(Janus.Metadata.CONTACTS_2_GIVENNAME.val()));
      contact.setSurName((String) metadata.get(Janus.Metadata.CONTACTS_2_SURNAME.val()));
      Object phone = metadata.get(Janus.Metadata.CONTACTS_2_TELEPHONE.val());
      contact.setTelephoneNumber(getPhoneAsString(phone));
      contact.setType(Contact.Type.valueOf((String) metadata.get(Janus.Metadata.CONTACTS_2_TYPE.val())));
      em.addContact(contact);
    }

    return em;
  }

  private static boolean emptyString(Object o) {
    return !(o instanceof String) || "".equals(((String) o).trim());
  }

  /**
   * The value of the phone number sometimes autocasts to an Integer
   *
   * @param p Object that may contain the phone number
   * @return String value of the phone number, can be {@literal null}
   */
  private static String getPhoneAsString(Object p) {
    String phone = null;
    if (p instanceof String) {
      phone = (String) p;

    } else if (p instanceof Integer) {
      phone = p.toString();
    }
    return phone;
  }

  private void addContact(Contact contact) {
    contacts.add(contact);
  }

  public void setAppTitle(String appTitle) {
    this.appTitle = appTitle;
  }

  public void setAppIcon(String appIcon) {
    this.appIcon = appIcon;
  }

  public void setAppDescription(String appDescription) {
    this.appDescription = appDescription;
  }

  public void setAppThumbNail(String appThumbNail) {
    this.appThumbNail = appThumbNail;
  }

  public void setAppEntityId(String appEntityId) {
    this.appEntityId = appEntityId;
  }

  public void setOauthCallbackUrl(String oauthCallbackUrl) {
    this.oauthCallbackUrl = oauthCallbackUrl;
  }

  public void setTwoLeggedOauthAllowed(boolean twoLeggedOauthAllowed) {
    this.twoLeggedOauthAllowed = twoLeggedOauthAllowed;
  }

  public void setOauthConsumerKey(String oauthConsumerKey) {
    this.oauthConsumerKey = oauthConsumerKey;
  }

  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
  }

  public String getAppTitle() {
    return appTitle;
  }

  public String getAppIcon() {
    return appIcon;
  }

  public String getAppDescription() {
    return appDescription;
  }

  public String getAppThumbNail() {
    return appThumbNail;
  }

  public String getAppEntityId() {
    return appEntityId;
  }

  public String getOauthCallbackUrl() {
    return oauthCallbackUrl;
  }

  public boolean isTwoLeggedOauthAllowed() {
    return twoLeggedOauthAllowed;
  }

  public String getOauthConsumerKey() {
    return oauthConsumerKey;
  }

  public void setOauthConsumerSecret(String oauthConsumerSecret) {
    this.oauthConsumerSecret = oauthConsumerSecret;
  }

  public String getOauthConsumerSecret() {
    return oauthConsumerSecret;
  }

  public String getAppLogoUrl() {
    return appLogoUrl;
  }

  public String getAppHomeUrl() {
    return appHomeUrl;
  }

  public void setAppLogoUrl(String appLogoUrl) {
    this.appLogoUrl = appLogoUrl;
  }

  public void setAppHomeUrl(String appHomeUrl) {
    this.appHomeUrl = appHomeUrl;
  }

  public List<Contact> getContacts() {
    return UnmodifiableList.decorate(contacts);
  }

  public boolean isIdpVisibleOnly() {
    return isIdpVisibleOnly;
  }

  public void setIdpVisibleOnly(boolean idpVisibleOnly) {
    isIdpVisibleOnly = idpVisibleOnly;
  }

  public String getEula() {
    return eula;
  }

  public void setEula(String eula) {
    this.eula = eula;
  }
}
