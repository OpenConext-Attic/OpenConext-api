<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%--
  Copyright 2012 SURFnet bv, The Netherlands

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>API 404</title>
<link rel="stylesheet"
	href="<c:url value="/css/bootstrap.min.css"/>"></link>
<link rel="stylesheet"
	href="<c:url value="/css/font-awesome.css"/>"></link>
<link rel="stylesheet" href="<c:url value="/css/oauth-client.css"/>"></link>
<script type="text/javascript"
	src="<c:url value="/js/bootstrap.min.js"/>"></script>
</head>
<body>
	<div class="page-header">
		<h2>SURFconext API</h2>
        <br/>
        <p><b>404.</b> That’s an error.</p>
        <p>The currently supported versions are <ins>/v1</ins></p>
	</div>
	<div class="row">
		<div class="span7 columns">
			<p>The SURFconext API application allows Service Providers to retrieve contextual information about users. 
              To query the API framework client please go to <a href="test">the playground</a></p>
		</div>
	</div>
    <hr/>
    <div class="row">
      <div class="span7 columns">
        <p>For more information please contact SURFnet | +31 302 305 305 | <a href="mailto:surfconext-beheer@surfnet.nl">surfconext-beheer@surfnet.nl</a></p>
      </div>
    </div>

</body>
</html>
