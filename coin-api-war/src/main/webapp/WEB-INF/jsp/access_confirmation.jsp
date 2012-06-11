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
  --%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page import="org.springframework.security.core.AuthenticationException" %>
<%@ page import="org.springframework.security.web.WebAttributes" %>
<%@ page import="org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="client" scope="request" type="nl.surfnet.coin.api.oauth.ExtendedBaseClientDetails"/>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="robots" content="noindex, nofollow"/>

  <title>SURFconext - ${client.clientMetaData.appTitle} requests your information</title>
  <meta name="viewport" content="width=device-width"/>

  <link href="https://static.dev.surfconext.nl/css/ext/jqueryjscrollpane/jquery.jscrollpane.css" rel="stylesheet" type="text/css" />
  <link href="https://static.dev.surfconext.nl/css/responsive/screen.css" rel="stylesheet" type="text/css" media="screen"/>
  <link href="<c:url value="/css/access_confirmation.css"/>" rel="stylesheet" type="text/css" media="screen"/>
  <link href="<c:url value="/css/font-awesome.css"/>" rel="stylesheet" type="text/css" media="screen"/>

</head>
<body>
<div id="wrapper">
  <!-- MAIN BOX -->
  <div id="main">

    <!-- Language selection -->
    <form id="LangForm" action="" method="post">
      <div>
        <input type="hidden" name="lang" id="LangVar" />
        <input type="hidden" name="show-help" id="showHelp" />
      </div>
      <ul class="nav">
        <li id="help_nav">
          <a href="#">help</a>
        </li>
        <li class="active">
          <a id="SubmitEnForm" href="#">en</a>
        </li>
        <li class="">
          <a id="SubmitNlForm" href="#">nl</a>
        </li>
      </ul>
    </form>

    <!-- Subheader -->
    <h1>${client.clientMetaData.appTitle} requests your information</h1>

    <!-- Main content -->
    <div id="content">
      <p class="introstrong">
        ${client.clientMetaData.appTitle}  requests this information that <c:out value="${header.schacHomeOrganization}" default="No Home Organization provided" /> has stored for you:</p>

      <div class="logos">

	 <span class="groups">
		<i class="icon-group"></i>
	 </span>

	 <span class="arrow">
		<i class="icon-arrow-right"></i>
	 </span>

        <img class=""
             alt="${client.clientMetaData.appTitle}"
             title="${client.clientMetaData.appTitle}"
             src="${client.clientMetaData.appIcon}"/>
      </div>

      <ul class="attributes">
        <li>Groups</li>
        <li>information about group members</li>
      </ul>

      <p>
        This information will be stored in SURFconext and passed on to ${client.clientMetaData.appTitle}. Terms of service of
        <a href="https://wiki.surfnetlabs.nl/display/conextsupport/Terms+of+Service+(EN)" target="_blank">SURFconext</a>
        <img src="https://static.dev.surfconext.nl/media/new_window_icon.gif"
             alt="(opens in a new window)" class="newwindow"/> and <a
          href="${client.clientMetaData.eulaUrl}">${client.clientMetaData.appTitle}</a> <img
          src="https://static.dev.surfconext.nl/media/new_window_icon.gif"
          alt="(opens in a new window)" class="newwindow"/> apply.
      </p>


      <div id="approve">
        <!-- YES -->
        <form id="accept" method="post" action="<%=request.getContextPath()%>/oauth/authorize">
          <p>
            <input name="user_oauth_approval" value="true" type="hidden"/>

            <input id="accept_terms_button"
                   class="submit bigbutton"
                   type="submit"
                   value="Allow"
                   style="font-weight: bold;" />
          </p>
        </form>

        <!-- NO -->
        <form id="reject" method="post" action="<%=request.getContextPath()%>/oauth/authorize">
          <p>
            <input name="user_oauth_approval" value="false" type="hidden"/>

            <input id="decline_terms_button"
                   class="submit bigbutton"
                   type="submit"
                   value="Deny" />
          </p>
        </form>
      </div>

      <p>This message only appears when you log in at a new service or when the information, passed to the service, is changed.</p>
    </div>

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