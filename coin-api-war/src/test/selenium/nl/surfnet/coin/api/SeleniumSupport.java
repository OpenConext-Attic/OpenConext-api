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
package nl.surfnet.coin.api;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * 
 *
 */
public class SeleniumSupport {

  private static FirefoxDriver driver;

  public static final String URL_UNDER_TEST = withEndingSlash(System
      .getProperty("selenium.test.url", "http://localhost:8090/coin-api-war"));

  private static String withEndingSlash(String path) {
    return path.endsWith("/") ? path : path + "/";
  }

  @Before
  public void initializeOnce() {
    if (driver == null) {
      SeleniumSupport.driver = new FirefoxDriver();
      SeleniumSupport.driver.manage().timeouts()
          .implicitlyWait(3, TimeUnit.SECONDS);
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          if (driver != null) {
            driver.quit();
          }
        }
      });
    }
  }

  /**
   * @return the webDriver
   */
  protected FirefoxDriver getWebDriver() {
    return driver;
  }
}
