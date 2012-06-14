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

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import nl.surfnet.coin.api.controller.AccessConfirmationController;

import static org.junit.Assert.assertThat;

public class AccessConfirmationControllerTest {

  @Test
  public void test() {
    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/foobar123");
    String url = AccessConfirmationController.getUrlWithLanguageParam(req, "tr");
    assertThat(url, IsEqual.equalTo("?lang=tr"));

    // see that a current lang-parameter is replaced
    req.setQueryString("lang=abc&def=123");
    url = AccessConfirmationController.getUrlWithLanguageParam(req, "tr");
    assertThat(url, IsEqual.equalTo("?def=123&lang=tr"));

    // see that a not-lang-parameter is not touched
    req.setQueryString("notlang=abc");
    url = AccessConfirmationController.getUrlWithLanguageParam(req, "tr");
    assertThat(url, IsEqual.equalTo("?notlang=abc&lang=tr"));

  }
}
