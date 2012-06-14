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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:useBean id="staticContentBasePath" scope="request" type="java.lang.String"/>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="robots" content="noindex, nofollow"/>

  <title><spring:message code="consent.window-title" arguments="${clientAppTitle}"/></title>
  <meta name="viewport" content="width=device-width"/>

  <link href="${staticContentBasePath}/css/responsive/screen.css" rel="stylesheet" type="text/css" media="screen"/>
  <link href="<c:url value="/css/access_confirmation.css"/>" rel="stylesheet" type="text/css" media="screen"/>
  <link href="<c:url value="/css/font-awesome.css"/>" rel="stylesheet" type="text/css" media="screen"/>

</head>
<body>
<div id="wrapper">
  <!-- MAIN BOX -->
  <div id="main">
    <!-- Language selection -->
      <ul class="nav">
        <li id="help_nav">
          <a id="helpLink" href="#"><spring:message code="consent.help-link" /></a>
        </li>
        <li class="<c:out value="${locale eq 'en' ? 'active' : ''}" />">
          <a id="enLink" href="<c:out value="${languageLinks.en}" />"><spring:message code="consent.change-locale-en" /></a>
        </li>
        <li class="<c:out value="${locale eq 'nl' ? 'active' : ''}" />">
          <a id="nlLink" href="<c:out value="${languageLinks.nl}" />"><spring:message code="consent.change-locale-nl" /></a>
        </li>
      </ul>

    <!-- Subheader -->
    <h1><spring:message code="consent.page-title" arguments="${clientAppTitle}"/></h1>

    <c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION"/>
    <authz:authorize ifAllGranted="ROLE_USER">

    <!-- Main content -->
    <div id="content">
      <p class="introstrong">
        <spring:message code="consent.intro-paragraph" arguments="${clientAppTitle}, ${userSchacHomeOrganization}" />
      </p>

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
        <li><spring:message code="consent.attributes.groups" /></li>
        <li><spring:message code="consent.attributes.group-members" /></li>
      </ul>

      <c:set var="newWindowImage" value="<img src=\"${staticContentBasePath}/media/new_window_icon.gif\"
      alt=\"(opens in a new window)\" class=\"newwindow\"/>" />
      <c:set var="conextTermsUrl" value="https://wiki.surfnetlabs.nl/display/conextsupport/Terms+of+Service+(EN)" />
      <p>
        <spring:message code="consent.terms.paragraph"
        arguments="${clientAppTitle}, ${conextTermsUrl}, ${newWindowImage}, ${clientEulaUrl}, ${clientAppTitle}, ${newWindowImage}" />
      </p>
  </authz:authorize>
