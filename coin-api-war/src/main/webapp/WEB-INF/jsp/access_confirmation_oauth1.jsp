<%@ page import="org.springframework.security.core.AuthenticationException" %>
<%@ page import="org.springframework.security.web.WebAttributes" %>
<%@ page import="org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
<!DOCTYPE html>
<html lang="en-US">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>OAuth Authorization</title>
  <link type="text/css" rel="stylesheet" href="<c:url value="/css/oauth.css"/>"/>
  <link href="/css/oauth.css" rel="stylesheet" type="text/css" />
  <!--[if lt IE 8 ]><link href="<c:url value="/css/oauth_ie.css"/>" rel="stylesheet" type="text/css" media="screen" /><![endif]-->
</head>
<body class="index">
<div id="wrapper">
  <div id="header"><img class="app-icon" src="${client.clientId}"/> <strong><c:out value="${client.clientId}" default="No Title"/></strong> is trying to access your information</div>
  <div id="main">
    <ul class="nav">
      <li class="active"><a href="#">EN</a></li>
    </ul>

    <h1>Do you want to grant access to <c:out value="${client.clientId}" default="No Title"/>?</h1>

    <% if (session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) != null && !(session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) instanceof UnapprovedClientAuthenticationException)) { %>
    <div class="error">
      <h2>Woops!</h2>

      <p>Access could not be granted. (<%= ((AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)).getMessage() %>)</p>
    </div>
    <% } %>
    <c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION"/>

    <authz:authorize ifAllGranted="ROLE_USER">

    <p>The application below is trying to access your group information. This application will be able to access your groups and information about your group members.</p>
    <div class="main-top"></div>
    <div class="main">
      <span class="category top"><strong>You are logged in as:</strong></span>
      <div class="column-container">
        <div class="column first-column">
          <dl>
            <dt>Display Name:</dt>
            <dd><c:out value="${header.displayName}" default="No Display Name provided" /></dd>
            <dt>User ID:</dt>
            <dd class="last user-id" id="UserId">${client.clientId}</dd>
          </dl>
        </div>
        <div class="column">
          <dl>
            <dt>Home Organization:</dt>
            <dd class="last"><c:out value="${header.schacHomeOrganization}" default="No Home Organization provided" /></dd>
          </dl>
        </div>
      </div>
      <span class="category"><strong>Application Details:</strong></span>
      <img class="app-thumb" src="${appThumbnail}" align="right" alt="application thumbnail" />
      <span class="description"><c:out value="${appDesc}" default="This application has no description."/></span>
      <div class="form">
        <form name="authZFormGrant" action="<%=request.getContextPath()%>/oauth/authorize" method="POST">
        <input name="oauth_token" value="<c:out value="${oauth_token}"/>" type="hidden"/>
        <c:if test="${!empty oauth_callback}">
          <input name="callbackURL" value="<c:out value="${oauth_callback}"/>" type="hidden"/>
        </c:if>
        <label><input name="authorize" value="Authorize" type="submit"></label>
        </form>

      </div>
    </div>
    <div class="main-bottom"></div>
    <div class="bottom">
      <p>This service is made possible by <a href='http://www.surfnet.nl/'>SURFnet</a>.</p>
    </div>
  </div>
  </authz:authorize>

</div>
</body>
</html>