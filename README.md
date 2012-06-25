OpenConext API
==============

This module provides an OpenSocial interface to OpenConext. It is licensed under the Apache 2 license.

Using the OpenConext OpenSocial API
------------------------------
        OpenConextOAuthClient api = new OpenConextOAuthClientImpl();
        api.setEndpointBaseUrl("https://api.surfconext.nl/v1/"); // (1)
        api.setConsumerKey("key");
        api.setConsumerSecret("secret");

        api.getPerson("urn:collab:person:test.surfguest.nl:gvanderploeg", null);

(1)  Note that 'v1' in this URL does not mean OAuth 1.0a, but rather version 1 of the entire OpenConext API.


Or wire it using Spring XML:

    <bean id="apiClient" class="nl.surfnet.coin.api.client.OpenConextOAuthClientImpl">
      <property name="endpointBaseUrl" value="${api-location}" />
      <property name="consumerKey" value="${oauth-key}"/>
      <property name="consumerSecret" value="${oauth-secret}" />
      <property name="version" value="v10a" />
    </bean>

This yields:

    {
      "startIndex":0,
      "totalResults":1,
      "itemsPerPage":1,
      "filtered":false,
      "updatedSince":false,
      "sorted":false,
      "entry": {
        "nickname":"Geert van der Ploeg",
        "emails":[{"value":"gvanderploeg@flex.surfnet.nl","type":"email"}],
        "id":"urn:collab:person:test.surfguest.nl:gvanderploeg",
        "name":{"formatted":"Geert van der Ploeg","familyName":"van der Ploeg","givenName":"Geert"},
        "tags":["guest"],
        "accounts":[{"username":"gvanderploeg","userId":"gvanderploeg"}],
        "displayName":"Geert van der Ploeg",
        "voot_membership_role":null,
        "organizations":[{"name":"test.surfguest.nl","type":null,"department":null,"title":null}],
        "phoneNumbers":null,
        "error":null
      }
    }
