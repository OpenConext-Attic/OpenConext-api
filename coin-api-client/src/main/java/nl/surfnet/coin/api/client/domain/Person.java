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

package nl.surfnet.coin.api.client.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Person representation
 * 
 */
public class Person {

  private String nickname;
  private Set<Email> emails;
  private String id;
  private Name name;
  private Set<String> tags;
  private Set<Account> accounts;
  private String displayName;
  private String voot_membership_role;
  private Set<Organization> organizations;
  private Set<PhoneNumber> phoneNumbers;
  private String error;

  public void addEmail(Email email) {
    if (emails == null) {
      emails = new HashSet<Email>();
    }
    emails.add(email);
  }

  public void addTag(String tag) {
    if (tags == null) {
      tags = new HashSet<String>();
    }
    tags.add(tag);
  }

  public void addAccount(Account account) {
    if (accounts == null) {
      accounts = new HashSet<Account>();
    }
    accounts.add(account);
  }

  public void addOrganization(Organization organization) {
    if (organizations == null) {
      organizations = new HashSet<Organization>();
    }
    organizations.add(organization);
  }

  public void addPhoneNumber(PhoneNumber phoneNumber) {
    if (phoneNumbers == null) {
      phoneNumbers = new HashSet<PhoneNumber>();
    }
    phoneNumbers.add(phoneNumber);
  }

  /**
   * @return the nickName
   */
  public String getNickname() {
    return nickname;
  }

  /**
   * @param nickName
   *          the nickName to set
   */
  public void setNickname(String nickName) {
    this.nickname = nickName;
  }

  /**
   * @return the emails
   */
  public Set<Email> getEmails() {
    return emails;
  }

  /**
   * @param emails
   *          the emails to set
   */
  public void setEmails(Set<Email> emails) {
    this.emails = emails;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the name
   */
  public Name getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(Name name) {
    this.name = name;
  }

  /**
   * @return the tags
   */
  public Set<String> getTags() {
    return tags;
  }

  /**
   * @param tags
   *          the tags to set
   */
  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  /**
   * @return the accounts
   */
  public Set<Account> getAccounts() {
    return accounts;
  }

  /**
   * @param accounts
   *          the accounts to set
   */
  public void setAccounts(Set<Account> accounts) {
    this.accounts = accounts;
  }

  /**
   * @return the displayName
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * @param displayName
   *          the displayName to set
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /**
   * @return the voot_membership_role
   */
  public String getVoot_membership_role() {
    return voot_membership_role;
  }

  /**
   * @param voot_membership_role
   *          the voot_membership_role to set
   */
  public void setVoot_membership_role(String voot_membership_role) {
    this.voot_membership_role = voot_membership_role;
  }

  /**
   * @return the organizations
   */
  public Set<Organization> getOrganizations() {
    return organizations;
  }

  /**
   * @param organizations
   *          the organizations to set
   */
  public void setOrganizations(Set<Organization> organizations) {
    this.organizations = organizations;
  }

  /**
   * @return the phoneNumbers
   */
  public Set<PhoneNumber> getPhoneNumbers() {
    return phoneNumbers;
  }

  /**
   * @param phoneNumbers
   *          the phoneNumbers to set
   */
  public void setPhoneNumbers(Set<PhoneNumber> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

  /**
   * @return the error
   */
  public String getError() {
    return error;
  }

  /**
   * @param error
   *          the error to set
   */
  public void setError(String error) {
    this.error = error;
  }

}
