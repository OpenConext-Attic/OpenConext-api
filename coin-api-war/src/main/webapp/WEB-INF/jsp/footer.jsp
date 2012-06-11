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

<!-- Help container (content injected with AJAX) -->
<div id="help" style="display: none">
</div>

<!-- Footer -->
<div class="bottom">
  <hr />
  <p>
    This service is powered by <a href="http://www.surfconext.nl/">SURFconext</a> - brought to you by SURFnet.
  </p>
</div>
</div>
</div>

<!-- JAVSCRIPT -->
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/jquery-1.5.1.min.js"></script>
<script type="text/javascript">
  $(document).ready(function() {
    $('#SubmitEnForm').click(function(e) {
      e.preventDefault();
      $('#showHelp').attr('value', $('#help').is(':visible') ? 'yes' : 'no');
      $('#LangVar').attr('value', 'en');
      $('#LangForm').submit();
    });

    $('#SubmitNlForm').click(function(e) {
      e.preventDefault();
      $('#showHelp').attr('value', $('#help').is(':visible') ? 'yes' : 'no');
      $('#LangVar').attr('value', 'nl');
      $('#LangForm').submit();
    });
  });
</script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/jquery.mousewheel.min.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/jquery.putCursorAtEnd.1.0.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/jquery.jscrollpane.min.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/jquery.tmpl.min.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/keyboardNavigator.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/typewatch.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/jquery.cookie.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/discover.js"></script>
<script type="text/javascript" src="https://static.dev.surfconext.nl/javascript/screen.js"></script>
<script type="text/javascript">
  //<!--
  $("#accept_terms_button").focus();    //-->
</script>
<script type="text/javascript">
  //<!--

  (function() {
    var d = new Discover();
    d.linkHelp();
  })();    //-->
</script>
<script type="text/javascript">
  //<!--

  //Create scrollbar
  $("#scrollViewport").jScrollPane({
    maintainPosition: false,
    enableKeyboardNavigation: true,
    showArrows: true
  });    //-->
</script>        <script type="text/javascript">
  jQuery(document).ready(function(){
    $('#faq h4').next().toggle();
    $('#faq h4').click(function() {
      $(this).next().toggle('slow');
      return false;
    }).next().hide();
  });
</script>
</body>
</html>