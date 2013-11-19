package nl.surfnet.coin.selenium;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import javax.sound.midi.SysexMessage;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Oauth20ClientCredentialsTestSelenium  extends SeleniumSupport {
 
  @Before
  public void getFreshDriver() {
    getRestartedWebDriver();
  }
  
  @Test
  public void clientCredentialsAgainstMock() {
    setupClientCredentials();
    enterText(getWebDriver().findElement(By.id("requestURL")), "http://localhost:8095/api/mock10/social/rest/people/person-foo");
    clickElementById("submit-step3");
    String responseInfo = getWebDriver().findElement(By.id("responseInfo")).getText();
    String rawResponse = getWebDriver().findElement(By.id("raw-response")).getText();
    waitForElementDisplay(getWebDriver().findElement(By.id("raw-response")));
    assertTrue("should have received 200 OK", responseInfo.contains("HTTP/1.1 200 OK"));
    assertTrue("should have received Mister Nice", rawResponse.contains("Mister Nice"));
  }

  private void setupClientCredentials() {
    getWebDriver().navigate().to("http://localhost:8095/api/test");
    WebElement oauthKey = getWebDriver().findElement(By.id("oauthKey"));
    enterText(oauthKey, "https://testsp.dev.surfconext.nl/shibboleth");
    clickElementById("clientCredentials");
    clickElementById("submit-step1");
    clickElementById("submit-step2");
    WebElement accessToken = getWebDriver().findElement(By.id("accessTokenValue"));
    while(!(accessToken.isDisplayed())) {
      System.out.println("find accessToken again");
      accessToken = getWebDriver().findElement(By.id("accessTokenValue"));
    }
    assertNotNull(accessToken.getText());
    assertFalse("accessToken should contain a value", StringUtils.isBlank(accessToken.getText()));
  }
  
  @Test
  public void clientCredentialsAgainstImpl() {
    setupClientCredentials();
    enterText(getWebDriver().findElement(By.id("requestURL")), "http://localhost:8095/api/social/rest/people/urn:collab:person:test.surfguest.nl:mfoo");
    clickElementById("submit-step3");
    String responseInfo = getWebDriver().findElement(By.id("responseInfo")).getText();
    String rawResponse = getWebDriver().findElement(By.id("raw-response")).getText();
    assertTrue("should have received 200 OK", responseInfo.contains("HTTP/1.1 200 OK"));
    assertTrue("should have received Mister Nice", rawResponse.contains("\"totalResults\":1"));
  }
}
