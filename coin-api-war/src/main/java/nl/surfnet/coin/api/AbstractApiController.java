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

package nl.surfnet.coin.api;

import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.util.StringUtils;

import nl.surfnet.coin.api.client.domain.AbstractEntry;

public abstract class AbstractApiController {

  /**
   * Get the username of the (via oauth) authenticated user that performs this request.
   *
   * @return the username in case of an end user authorized request (3 legged oauth1, authorization code grant oauth2) or the consumer key in case of unauthorized requests.
   */
  public String getOnBehalfOf() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      return null;
    } else {
      if (auth.getPrincipal() instanceof ConsumerDetails) {
        // Two legged, it does not have end user details
        return null;
      } else if (auth.getPrincipal() instanceof String) {
        return (String) auth.getPrincipal();
      } else {
        return ((UserDetails) auth.getPrincipal()).getUsername();
      }
    }
  }
  /**
   * Filter/mangle a result set based on query parameters
   * @param parent the root object; effectively this parameter is altered by setting the totalResults property
   * @param count nr of records to fetch
   * @param startIndex the start index
   * @param sortBy field to sort by
   * @param entry the result list of entries
   * @return
   */
  protected List<? extends Object> processQueryOptions(AbstractEntry parent, Integer count, Integer startIndex,
                                                     String sortBy, List<? extends Object> entry) {
    parent.setTotalResults(entry.size());
    if (StringUtils.hasText(sortBy)) {
      BeanComparator comparator = new BeanComparator(sortBy);
      Collections.sort(entry, comparator);
      parent.setSorted(true);
    }
    if (startIndex != null) {
      entry = entry.subList(startIndex, entry.size());
      parent.setStartIndex(startIndex);
    }
    if (count != null) {
      entry = entry.subList(0, count);
      parent.setItemsPerPage(count);
    }

    return entry;
  }

}
