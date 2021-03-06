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
import org.junit.Rule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By.ByName;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ByIdOrName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 *
 *
 */
public class SeleniumSupport {
  
  private final static Logger LOG = LoggerFactory.getLogger(SeleniumSupport.class);

  private static WebDriver driver;

  private static final String MUJINA_BASE = "http://localhost:8095/mujina-idp";
  protected static final String USER_ID = "mocked-user";

  protected String getApiBaseUrl() {
    return System.getProperty("selenium.test.url", "http://localhost:8095/api/");
  }

  @Rule
  public SaveStateToFilesTestRule saveStateRule = new SaveStateToFilesTestRule(driver);


  @Before
  public void initializeOnce() {
    if (driver == null) {
      if ("firefox".equals(System.getProperty("selenium.webdriver", "firefox"))) {
        initFirefoxDriver();
      } else {
        initPhantomJSDriver();
      }
    }
  }

  private void initPhantomJSDriver() {
      driver = new PhantomJSDriver();
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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
    driver = new FirefoxDriver();

    driver.manage().timeouts()
        .implicitlyWait(10, TimeUnit.SECONDS);
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

  protected WebDriver getRestartedWebDriver() {
    driver.quit();
    driver = null;
    initializeOnce();
    return driver;
  }


  public void letMujinaSendUrnCollabAttribute(String user) {
    ClientConfig config = new DefaultClientConfig();
    Client.create(config)
      .resource(MUJINA_BASE + "/api/attributes/urn:oid:1.3.6.1.4.1.1076.20.40.40.1")
      .type("application/json")
      .put("{\"value\": \"" + user + "\"}");
  }

  public void loginAtMujinaIfNeeded(String username) {

    try {
      getWebDriver().findElement(By.name("j_username"));
    } catch (RuntimeException e) {
      LOG.debug("No login form found, probably no login @mujina needed anymore.");
      return;
    }

    getWebDriver().findElement(By.name("j_username")).sendKeys(username);
    getWebDriver().findElement(By.name("j_password")).sendKeys("secret");
    getWebDriver().findElement(By.name("login")).submit();
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

  protected void clickElementById(String element) {
    waitForElementDisplay(getWebDriver().findElement(By.id(element)));
    getWebDriver().findElement(By.id(element)).click();
  }
  
  protected void waitForElementDisplay(WebElement element) {
    while(!element.isDisplayed()) {
      LOG.debug("waiting for element " + element.getAttribute("id"));
      //sorry I need to eat your CPU here
      try {
        Thread.sleep(100);
      }catch (InterruptedException e) {
        //ignored
      }
    }
  }
  
  protected void enterText(WebElement element, CharSequence keysToSend) {
    waitForElementDisplay(element);
    element.clear();
    element.sendKeys(keysToSend);
  }
}
