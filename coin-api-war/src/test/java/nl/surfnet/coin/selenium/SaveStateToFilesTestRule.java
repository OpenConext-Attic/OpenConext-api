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
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extension for JUnit tests that outputs the current state of the browser to
 * <ul>
 *   <li>a text file (the DOM)</li>
 *   <li>a png file (screenshot)</li>
 * </ul>
 *
 * <p>
 * Usage:
 * Add a @Rule annotation to a member of the (base) test class:
 * <pre>
 * &#064;Rule
 * public SaveStateToFilesTestRule saveStateRule = new SaveStateToFilesTestRule(driver);
 * </pre>
 * </p>
 *
 *
 * @author Geert van der Ploeg
 */
public class SaveStateToFilesTestRule implements TestRule {

  private static final Logger LOG = LoggerFactory.getLogger(SaveStateToFilesTestRule.class);

  private WebDriver driver;

  public SaveStateToFilesTestRule(WebDriver driver) {
    this.driver = driver;
  }

  @Override
  public Statement apply(final Statement statement, final Description description) {

    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          statement.evaluate();
        } catch (Throwable t) {
          // exception will be thrown only when a test fails.

          captureScreenshot(description);
          captureDom(description);

          // rethrow to allow the failure to be reported by JUnit
          throw t;
        }
      }

      public void captureDom(Description description) {
        try {
          String dom = driver.getPageSource();
          String filename = temporaryFilename("dom", "html");
          IOUtils.write(dom, new FileWriter(filename));
          LOG.info("DOM for failed method {}.{}() will be saved to: {}", new Object[]{
            description.getClassName(), description.getMethodName(), filename});

        } catch (Exception e) {
          // No need to crash the tests if the screenshot fails
          LOG.debug("Saving the dom failed: {}", e.getMessage());
        }
      }

      public void captureScreenshot(Description description) {
        try {
          File screenshot =  ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
          String outputFile = temporaryFilename("screenshot", "png");

          FileUtils.copyFile(screenshot, new File(outputFile));
          LOG.info("Screenshot for failed method {}.{}() will be saved to: {}", new Object[] {
            description.getClassName(), description.getMethodName(), outputFile});
        } catch (Exception e) {
          // No need to crash the tests if the screenshot fails
          LOG.debug("Saving a screenshot failed: {}", e.getMessage());
        }
      }

      private String temporaryFilename(String nameBase, String extension) {
        return String.format("%s%s%s-%d.%s",
          System.getProperty("java.io.tmpdir"),
          System.getProperty("file.separator"),
          nameBase,
          System.currentTimeMillis(),
          extension);
      }
    };
  }
}
