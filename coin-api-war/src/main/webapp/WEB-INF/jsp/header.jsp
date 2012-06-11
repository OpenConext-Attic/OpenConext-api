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
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="staticContentBasePath" scope="request" type="java.lang.String"/>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="robots" content="noindex, nofollow"/>

  <title>SURFconext - ${clientAppTitle} requests your information</title>
  <meta name="viewport" content="width=device-width"/>

  <link href="${staticContentBasePath}/css/ext/jqueryjscrollpane/jquery.jscrollpane.css" rel="stylesheet"
        type="text/css"
      />
  <link href="${staticContentBasePath}/css/responsive/screen.css" rel="stylesheet" type="text/css" media="screen"/>
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
    <h1>${clientAppTitle} requests your information</h1>

    <c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION"/>
    <authz:authorize ifAllGranted="ROLE_USER">

    <!-- Main content -->
    <div id="content">
      <p class="introstrong">
          ${clientAppTitle}  requests this information that <c:out value="${userSchacHomeOrganization}"
                                                                   default="No Home Organization provided" /> has stored for you:</p>

      <div class="logos">

	 <span class="groups">
		<i class="icon-group"></i>
	 </span>

	 <span class="arrow">
		<i class="icon-arrow-right"></i>
	 </span>

        <img class=""
             alt="${clientAppTitle}"
             title="${clientAppTitle}"
             src="${clientAppIcon}"/>
      </div>

      <ul class="attributes">
        <li>Groups</li>
        <li>information about group members</li>
      </ul>

      <p>
        This information will be stored in SURFconext and passed on to ${clientAppTitle}. Terms of service of
        <a href="https://wiki.surfnetlabs.nl/display/conextsupport/Terms+of+Service+(EN)" target="_blank">SURFconext</a>
        <img src="${staticContentBasePath}/media/new_window_icon.gif"
             alt="(opens in a new window)" class="newwindow"/> and <a
          href="${clientEulaUrl}">${clientAppTitle}</a> <img
          src="${staticContentBasePath}/media/new_window_icon.gif"
          alt="(opens in a new window)" class="newwindow"/> apply.
      </p>
  </authz:authorize>
