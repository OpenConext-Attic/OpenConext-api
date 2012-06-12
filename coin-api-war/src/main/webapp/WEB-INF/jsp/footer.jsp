<%--
~ Copyright 2012 SURFnet bv, The Netherlands
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~      http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="helpContainer" style="display: none">
  <h3><spring:message code="consent.help-title"/></h3>
  <spring:message code="consent.help-paragraph"/>
    <input id="helpOk"
           class="submit bigbutton"
           type="button"
           value="<spring:message code="consent.help.backButton" />"
           style="font-weight: bold;" />
</div>

<!-- Footer -->
<div class="bottom">
  <hr />
  <p>
    <spring:message code="consent.footer-paragraph"/>
  </p>
</div>
</div>
</div>

<!-- JAVSCRIPT -->
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/jquery-1.5.1.min.js"></script>
<script type="text/javascript">
  $(document).ready(function() {
    $('#helpLink').click(function() {
      $("#helpContainer").toggle();
      $("#content").toggle();
    });
    $("#helpOk").click(function() {
      $("#helpContainer").toggle();
      $("#content").toggle();
    });
  });
</script>
<!--
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/jquery.mousewheel.min.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/jquery.putCursorAtEnd.1.0.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/jquery.jscrollpane.min.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/jquery.tmpl.min.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/keyboardNavigator.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/typewatch.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/jquery.cookie.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/discover.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/screen.js"></script>
-->
<script type="text/javascript">

  $("#accept_terms_button").focus();

  (function() {
    var d = new Discover();
    d.linkHelp();
  })();
</script>

</body>
</html>