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

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.DefaultKeyGenerator;
import org.springframework.cache.interceptor.KeyGenerator;

/**
 * {@link KeyGenerator} that also takes into account the methodName when
 * generating keys. This appeared to be necessary in JanusClientDetailsService
 * (see the #testCache method in the corresponding unit test class)
 * 
 */
public class MethodNameAwareCacheKeyGenerator extends DefaultKeyGenerator implements KeyGenerator {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.cache.interceptor.KeyGenerator#generate(java.lang.Object
   * , java.lang.reflect.Method, java.lang.Object[])
   */
  @Override
  public Object generate(Object target, Method method, Object... params) {
    Object hash = super.generate(target, method, params);
    return new StringBuilder(hash != null ? hash.toString() : "31").append(method.getName()).toString();
  }

}
