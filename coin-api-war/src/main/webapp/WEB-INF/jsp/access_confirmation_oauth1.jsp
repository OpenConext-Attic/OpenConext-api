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
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:useBean id="client" scope="request" type="nl.surfnet.coin.api.oauth.ExtendedBaseConsumerDetails"/>
<c:set scope="request" var="clientAppTitle" value="${client.clientMetaData.appTitle}" />
<c:set scope="request" var="clientAppIcon" value="${client.clientMetaData.appIcon}" />
<c:set scope="request" var="clientAppEulaUrl" value="${client.clientMetaData.eulaUrl}"/>
<c:set scope="request" var="userSchacHomeOrganization" value="${client.clientMetaData.appEntityId}"/>

<jsp:include page="header.jsp" />

<authz:authorize ifAllGranted="ROLE_USER">
<spring:message var="acceptButtonText" code="consent.button.accept" />
<spring:message var="denyButtonText" code="consent.button.deny" />

  <div id="approve">
    <!-- YES -->
    <form id="accept" method="post" action="<%=request.getContextPath()%>/oauth1/authorize">
      <p>
        <input name="authorize" value="true" type="hidden"/>
        <input name="oauth_token" value="<c:out value="${oauth_token}"/>" type="hidden"/>
        <c:if test="${!empty oauth_callback}">
          <input name="callbackURL" value="<c:out value="${oauth_callback}"/>" type="hidden"/>
        </c:if>
        <input id="accept_terms_button"
               class="submit bigbutton"
               type="submit"
               value="${acceptButtonText}"
               style="font-weight: bold;" />
      </p>
    </form>

    <!-- NO -->
    <form id="reject" method="post" action="<%=request.getContextPath()%>">
      <p>
        <input name="user_oauth_approval" value="false" type="hidden"/>
        <input id="decline_terms_button" class="submit bigbutton"
               type="submit" value="${denyButtonText}" />
      </p>
    </form>
  </div>

  <p><spring:message code="consent.only-new-or-changed" /></p>
  </div>

</authz:authorize>

<jsp:include page="footer.jsp" />