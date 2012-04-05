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

package nl.surfnet.coin.teams.domain;

/**
 * Enum with the types of Group provider preconditions
 * <p/>
 * Needed for conversion of PHP class names from Engine block
 */
public enum GroupProviderPreconditionTypes {
  ACCESS_TOKEN_EXISTS("EngineBlock_Group_Provider_Precondition_OpenSocial_Oauth_AccessTokenExists"),
  USER_ID_REGEX("EngineBlock_Group_Provider_Precondition_UserId_PregMatch");

  private String groupProviderPreconditionType;

  GroupProviderPreconditionTypes(String groupProviderPreconditionType) {
    this.groupProviderPreconditionType = groupProviderPreconditionType;
  }

  public String getStringValue() {
    return groupProviderPreconditionType;
  }

  public static GroupProviderPreconditionTypes fromString(String typeAsString) {
    if (typeAsString != null) {
      for (GroupProviderPreconditionTypes type : GroupProviderPreconditionTypes.values()) {
        if (typeAsString.equalsIgnoreCase(type.getStringValue())) {
          return type;
        }
      }
    }
    return null;
  }

}
