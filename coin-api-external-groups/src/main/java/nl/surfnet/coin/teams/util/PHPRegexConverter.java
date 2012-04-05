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

import org.springframework.util.StringUtils;

/**
 * Util class to handle the regular expressions created for PHP
 */
public final class PHPRegexConverter {

  private PHPRegexConverter() {

  }

  /**
   * Strips the delimiter chars from the PHP regex
   *
   * @param phpPattern String with regular expression that may contain | as delimiter
   * @return cleaned up regex pattern, can be {@literal null}
   */
  public static String convertPHPRegexPattern(String phpPattern) {
    if (StringUtils.hasText(phpPattern)) {
      return phpPattern.replaceFirst("^\\|(.*)\\|$", "$1");
    }
    return phpPattern;
  }
}
