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
</head>
<body onLoad="doLogin();" class="index">
	<script type="text/javascript">
	function doLogin() {
	    document.forms[0].elements['authorize'].click();
	}
	</script>
    <c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION"/>
    <authz:authorize ifAllGranted="ROLE_USER">
    <div class="form" style="display:none;">
        <form name="authZFormGrant" action="<%=request.getContextPath()%>/oauth1/authorize" method="POST">
        <input name="oauth_token" value="<c:out value="${oauth_token}"/>" type="hidden"/>
        <c:if test="${!empty oauth_callback}">
          <input name="callbackURL" value="<c:out value="${oauth_callback}"/>" type="hidden"/>
        </c:if>
        <input name="authorize" value="Authorize" type="submit">
        </form>

      </div>
  </authz:authorize>
</body>
</html>