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

package nl.surfnet.coin.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * Subclass of {@link SimpleMappingExceptionResolver} that adds a property for classes to exclude from the exception
 * mapping.
 * A concrete use case is combining exception when using Spring Security: Spring Security relies on handling
 * {@link org.springframework.security.core.AuthenticationException AuthenticationExceptions} itself (for redirecting
 * to its {@link org.springframework.security.web.AuthenticationEntryPoint AuthenticationEntryPoint}. Therefore
 * you'd want to leave these alone.
 *
 */
public class ExclusionExceptionResolver extends SimpleMappingExceptionResolver implements InitializingBean {

  private static final Logger LOG = LoggerFactory.getLogger(ExclusionExceptionResolver.class);
  private Class[] excludedClasses;

  /**
   * {@inheritDoc}
   * <p>
   * This implementation checks whether the given exception <code>isInstance()</code> of one of the configured
   * excluded classes and returns null in case so.
   * </p>
   */
  @Override
  protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    for (Class<?> excludedClass : excludedClasses) {
      if (excludedClass.isInstance(ex)) {
        LOG.debug("The thrown exception {} is ignored for resolving because it is excluded as class {}",
            ex.getClass(), excludedClass);
        return null;
      }
    }
    return super.doResolveException(request, response, handler, ex);
  }

  /**
   * Provide a list of classes that should not be handled by this resolver.
   * @param excludedClasses Class[]
   */
  public void setExcludedClasses(Class[] excludedClasses) {
    this.excludedClasses = excludedClasses;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (excludedClasses == null) {
      excludedClasses = new Class[]{};
    }
  }
}
