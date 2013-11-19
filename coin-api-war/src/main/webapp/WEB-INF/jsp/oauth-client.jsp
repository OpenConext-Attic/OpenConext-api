<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
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
<title>Service Provider Social Queries</title>
<link rel="stylesheet"
	href="<c:url value="/css/bootstrap.min.css"/>"></link>
<link rel="stylesheet"
	href="<c:url value="/css/font-awesome.css"/>"></link>
<link rel="stylesheet" href="<c:url value="/css/oauth-client.css"/>"></link>
<script type="text/javascript"
	src="<c:url value="/js/jquery-1.7.2.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/bootstrap.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/oauth-client.js"/>"></script>
</head>
<body>
	<div class="page-header">
		<h1>
			<small>SURFconext API Playground</small>
		</h1>
	</div>
	<div class="row">
		<div class="span11 columns">
			<p>Play with the different OAuth versions and API scenario's and
				see the subsequent request and responses from the SURFconext
				OpenSocial API (version <c:out value="${versionIdentifier}" />).</p>
		</div>
	</div>
	<div class="row">
		<div class="span7 columns">
			<form:form action="${pageContext.request.contextPath}/test" commandName="settings"
				method="post" class="form-horizontal">
				<div class="accordion" id="mainOptions">
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse"
								data-parent="#mainOptions" href="#step1"> <span
								class="badge badge-info">1</span> OAuth Settings</a>
						</div>
						<div id="step1" class="accordion-body collapse">
							<div class="accordion-inner">
								<fieldset>
									<div class="control-group">
										<label class="control-label" for="version">OAuth
											Version</label>
										<div class="controls">
											<form:select id="version" path="version" items="${versions}" />
											<p class="help-block">Note: the OAuth endpoints are
												different per version in api.surfconext</p>
										</div>
										<div class="control-group">
											<label class="control-label" for="oauthKey">OAuth key</label>
											<div class="controls">
												<form:input path="oauthKey" id="oauthKey" name="oauthKey"
													class="input-xxlarge" />
												<p class="help-block">${settings.oauthKey}</p>
											</div>
										</div>
										<div class="control-group" id="secretInput"
											${settings.grantType!=
											'implicit' ? '' : 'style="display: none;"'}>
											<label class="control-label" for="oauthSecret">OAuth
												secret</label>
											<div class="controls">
												<form:input path="oauthSecret" id="oauthSecret"
													name="oauthSecret" class="input-xxlarge" />
											</div>
										</div>
										<div class="control-group" id="scope">
											<label class="control-label" for="scope">Scope</label>
											<div class="controls">
												<form:input path="scope" id="scope"
													name="scope" class="input-xxlarge" />
											</div>
										</div>
									</div>
									<div id="oauth10a" ${settings.version==
										'1.0a' ? '' : 'style="display: none;"'}>
										<div class="control-group">
											<label class="control-label" for="twoLegged">Two-Legged</label>
											<div class="controls">
												<label class="checkbox"> <form:checkbox
														id="twoLegged" name="twoLegged" path="twoLegged" /> </label>
											</div>
										</div>
										<div id="oauth10aInput" ${settings.twoLegged==
											false ? '' : 'style="display: none;"'}>
											<div class="control-group">
												<label class="control-label" for="requestTokenEndPoint">RequestToken
													URL</label>
												<div class="controls">
													<form:input path="requestTokenEndPoint"
														id="requestTokenEndPoint" name="requestTokenEndPoint"
														class="input-xxlarge" />
													<p class="help-block">Hint: ${settings.requestTokenEndPoint}</p>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label" for="requestTokenVerb">RequestToken
													HTTP Verb</label>
												<div class="controls">
													<form:select id="requestTokenVerb" path="requestTokenVerb" items="${requestTokenVerbs}" />
													<p class="help-block">The HTTP method to obtain the Request Token</p>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label" for="accessTokenEndPoint">AccessToken
													URL</label>
												<div class="controls">
													<form:input path="accessTokenEndPoint"
														id="accessTokenEndPoint" name="accessTokenEndPoint"
														class="input-xxlarge" />
													<p class="help-block">Hint: ${settings.accessTokenEndPoint}</p>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label" for="authorizationURL">Authorization
													URL</label>
												<div class="controls">
													<form:input path="authorizationURL" id="authorizationURL"
														name="authorizationURL" class="input-xxlarge" />
													<p class="help-block">Hint: ${settings.authorizationURL}</p>
												</div>
											</div>
										</div>
									</div>
									<div id="oauth20" ${settings.version==
										'2.0' ? '' : 'style="display: none;"'}>
										<div class="control-group">
											<label class="control-label" for="implicitGrant">Grant Type</label>
											<div class="controls">
												<label class="radio inline"><form:radiobutton
														id="implicitGrant" value="implicit"
														path="grantType" />Implicit</label>
												<label class="radio inline"><form:radiobutton
                            id="clientCredentials" value="clientCredentials"
                            path="grantType" />clientCredentials</label>
                        <label class="radio inline"><form:radiobutton
                            id="authCode" value="authCode"
                            path="grantType" />authorizationCode</label>
											</div>
										</div>
										<div id="oauth20Input" ${settings.grantType!=
											'implicit' ? '' : 'style="display: none;"'}>
											<div id="redirectURI" class="control-group">
												<label class="control-label" for="leaveOutRedirectUri">Use no redirect URI</label>
												<div class="controls">
													<label class="checkbox"> <form:checkbox
															id="leaveOutRedirectUri" name="leaveOutRedirectUri"
															path="leaveOutRedirectUri" /> </label>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label" for="accessTokenEndPoint2">AccessToken
													URL</label>
												<div class="controls">
													<form:input path="accessTokenEndPoint2"
														id="accessTokenEndPoint2" name="accessTokenEndPoint2"
														class="input-xxlarge" />
													<p class="help-block">Hint: ${settings.accessTokenEndPoint2}</p>
												</div>
											</div>
										</div>
										<div id="authorizationUrl" class="control-group">
											<label class="control-label" for="authorizationURL2">Authorization
												URL</label>
											<div class="controls">
												<form:input path="authorizationURL2" id="authorizationURL2"
													name="authorizationURL2" class="input-xxlarge" />
												<p class="help-block">Hint: ${settings.authorizationURL2}</p>
											</div>
										</div>
									</div>
									<div class="form-actions">
										<button id="submit-step1" name="step1" class="btn btn-primary">Next</button>
										<button name="reset" class="btn">Reset</button>
									</div>

								</fieldset>
							</div>
						</div>
					</div>
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse"
								data-parent="#mainOptions" href="#step2"> <span
								class="badge badge-info">2</span> OAuth Authorization</a>
						</div>
						<div id="step2" class="accordion-body collapse">
							<div class="accordion-inner">
								<fieldset>
									<div class="control-group" ${settings.grantType!=
                    'clientcredentials' ? '' : 'style="display: none;"'}>
										<label class="control-label">Authorization URL</label>
										<div class="controls">
											<p class="break-word">
												<c:out value="${authorizationUrlAfter}" />
											</p>
											<p class="help-block">Note: this is the URL to redirect
												to for user authentication</p>
										</div>
									</div>
									<div id="accessTokenRequestOptionsOauth2" class="control-group" ${settings.version==
										'2.0' ? '' : 'style="display: none;"'}>
										<label class="control-label" for="accessTokenRequestOptions">AccessToken request options</label>
										<div class="controls">
											<form:select id="accessTokenRequestOption" path="accessTokenRequestOption" items="${accessTokenRequestOptions}" />
											<p class="help-block">Note: the AccessToken oauth2 request method (see <a href="http://tools.ietf.org/html/draft-ietf-oauth-v2-26#section-4.1.3" target="_blank">http://tools.ietf.org/html/draft-ietf-oauth-v2-26#section-4.1.3</a>)</p>
										</div>
									</div>
									<div class="form-actions">
										<button id="submit-step2" name="step2" class="btn btn-primary">Next</button>
										<button name="reset" class="btn">Reset</button>
									</div>
								</fieldset>
							</div>
						</div>
					</div>
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse"
								data-parent="#mainOptions" href="#step3"> <span
								class="badge badge-info">3</span> OAuth Requests </a>
						</div>
						<div id="step3" class="accordion-body collapse">
							<div class="accordion-inner">
								<fieldset>
									<div class="control-group">
										<label class="control-label">Access token</label>
										<div class="controls">
											<p id="accessTokenValue" class="help-block">
												<c:out value="${accessToken.token}" />
											</p>
											<p class="help-block">Note: this is the accessToken for
												all subsequent OAuth queries</p>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label" for="count">Count </label>
										<div class="controls">
											<form:input path="count" id="count" name="count"
												class="input-mini" />
											<p class="help-block">The count query parameter is the
												maximum number of items (e.g. groups or teammembers) to
												return</p>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label" for="startIndex">Start
											Index </label>
										<div class="controls">
											<form:input path="startIndex" id="startIndex"
												name="startIndex" class="input-mini" />
											<p class="help-block">The startIndex query parameter
												determines how many items to skip (in order to support
												pagination)</p>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label" for="sortBy">Sorting </label>
										<div class="controls">
											<form:input path="sortBy" id="sortBy" name="sortBy"
												class="input-xlarge"/>
											<p class="help-block">The sortBy query parameter
												determines how items are sorted (sorting on groupMembers is not supported)</p>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label" for="requestURL">API
											Request </label>
										<div class="controls">
											<form:input path="requestURL" id="requestURL"
												name="requestURL" class="input-xxlarge" />
											<p class="help-block">Hint:
												https://api.DOMAIN_NAME/v1/social/rest/groups/@me</p>
                                            <p class="help-block">Hint:
                                              https://api.DOMAIN_NAME/v1/social/rest/people/@me/groupId</p>
									</div>
									<div class="form-actions">
										<button id="submit-step3" name="step3" class="btn btn-primary">Fetch</button>
										<button name="reset" class="btn">Reset</button>
									</div>
								</fieldset>
							</div>
						</div>
					</div>
				</div>
				<a class="btn btn-small btn-info" target="_blank"
					href="https://wiki.surfnetlabs.nl/display/conextdocumentation/api.surfconext.nl">
					<i class="icon-info-sign"></i> More Info</a>
				<input id="step" type="hidden"
					value="<c:out value="${settings.step}"/>" name="step" />
				<input id="parseAnchorForAccesstoken" type="hidden"
					value="<c:out value="${parseAnchorForAccesstoken}"/>" name="parseAnchorForAccesstoken" />
			</form:form>
		</div>
		<div class="span5 columns">
			<div id="request">
				<div class="alert alert-info alert-http">HTTP Request</div>
				<pre id="requestInfo" class="prettyprint pre-scrollable pre-json"><c:out value="${requestInfo}" /></pre>
			</div>
			<div id="response">
				<div class="alert alert-info alert-http">HTTP Response Headers</div>
				<pre id="responseInfo" class="prettyprint pre-scrollable pre-json"><c:out value="${responseInfo}" /></pre>
			</div>
			<div id="responseTime">
				<p class="help-block">
				<c:out value="${responseTime}" /></p>
			</div>
			<div id="raw-response">
				<div class="alert alert-info alert-http">HTTP Response Body </div>
				<pre id="rawResponseInfo" class="prettyprint pre-scrollable pre-json"><c:out value="${rawResponseInfo}" /></pre>
			</div>
		</div>
	</div>

</body>
</html>
