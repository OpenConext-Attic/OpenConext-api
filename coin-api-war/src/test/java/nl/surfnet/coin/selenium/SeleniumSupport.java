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

package nl.surfnet.coin.selenium;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class SeleniumSupport {
  
  private final static Logger LOG = LoggerFactory.getLogger(SeleniumSupport.class);

  private static WebDriver driver;

  private static String withEndingSlash(String path) {
    return path.endsWith("/") ? path : path + "/";
  }
  
  protected String getApiBaseUrl() {
    return System.getProperty("selenium.test.url", "http://localhost:8095/");
  }

  @Before
  public void initializeOnce() {
    if (driver == null) {
      if ("firefox".equals(System.getProperty("selenium.webdriver", "firefox"))) {
        initFirefoxDriver();
      } else {
        initHtmlUnitDriver();
      }
    }
  }

  private void initHtmlUnitDriver() {
    SeleniumSupport.driver = new HtmlUnitDriver();
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

  private void initFirefoxDriver() {
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

  /**
   * @return the webDriver
   */
  protected WebDriver getWebDriver() {
    return driver;
  }

  protected void loginEndUser() {
    // log in...
    getWebDriver().findElement(By.name("j_username")).sendKeys("bob");
    getWebDriver().findElement(By.name("j_password")).sendKeys("bobspassword");
    getWebDriver().findElement(By.name("submit")).click();
  }

  protected void giveUserConsentIfNeeded() {
    WebElement authorizeButton = null;
    try {
      authorizeButton = getWebDriver()
          .findElement(By.id("accept_terms_button"));
    } catch (RuntimeException e) {
      LOG.debug("No consent form found, probably no consent needed anymore.");
    }
    if (authorizeButton != null) {
      LOG.debug("Clicking 'authorize'-button on user consent form");
      authorizeButton.click();
    }
  }

}
