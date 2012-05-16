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
package nl.surfnet.coin.util;

import net.sf.ehcache.CacheManager;

import org.springframework.cache.support.NoOpCacheManager;

/**
 * Cache Manager that can deal with a {@link net.sf.ehcache.CacheManager} for
 * compatibility reasons, but does not actually cache anything
 * 
 */
public class ConextNoOpCacheManager extends NoOpCacheManager {

  private CacheManager cacheManager;

  public void setCacheManager(net.sf.ehcache.CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }
}
