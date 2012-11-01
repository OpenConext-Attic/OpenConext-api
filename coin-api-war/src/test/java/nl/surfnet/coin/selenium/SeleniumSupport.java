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

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

/**
 *
 *
 */
public class SeleniumSupport {
  
  private final static Logger LOG = LoggerFactory.getLogger(SeleniumSupport.class);

  private static WebDriver driver;

  private static final String MUJINA_BASE = "http://localhost:8095/mujina-idp";

  protected String getApiBaseUrl() {
    return System.getProperty("selenium.test.url", "http://localhost:8095/api/");
  }

  @Rule
  public ScreenshotTestRule screenshotTestRule = new ScreenshotTestRule();


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

  public void loginAtMujina() {
    getWebDriver().findElement(By.name("j_username")).sendKeys("user");
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

  public static class ScreenshotTestRule implements MethodRule {
    public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object o) {
      return new Statement() {
        @Override
        public void evaluate() throws Throwable {
          try {
            statement.evaluate();
          } catch (Throwable t) {
            // exception will be thrown only when a test fails.
            captureScreenshot(frameworkMethod);
            // rethrow to allow the failure to be reported by JUnit
            throw t;
          }
        }

        public void captureScreenshot(FrameworkMethod method) {
          try {
            File screenshot =  ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String outputFile = String.format("%s%s%d.png", System.getProperty("java.io.tmpdir"), System.getProperty("file.separator"), System.currentTimeMillis());

            LOG.info("Screenshot for failed method {}.{}() will be saved to: {}", new Object[] {
              method.getMethod().getDeclaringClass().getName(), method.getName(), outputFile});
            FileUtils.copyFile(screenshot, new File(outputFile));
          } catch (Exception e) {
            // No need to crash the tests if the screenshot fails
            LOG.debug("Saving a screenshot failed: {}", e.getMessage());
          }
        }
      };
    }
  }

}
