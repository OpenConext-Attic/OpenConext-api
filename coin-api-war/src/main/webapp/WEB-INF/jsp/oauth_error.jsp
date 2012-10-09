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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
  <title>API</title>
</head>
<body>
<h1>OAuth2 Error</h1>
<div id="content">
  <p><c:out value="${message}"/> (<c:out value="${error.message}"/>)</p>
  <p>Please go back to your client application and try again, or contact the owner and ask for support.</p>
</div>
</body>
</html>
