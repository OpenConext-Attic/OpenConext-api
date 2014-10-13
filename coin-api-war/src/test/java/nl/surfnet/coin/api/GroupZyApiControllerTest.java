/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.surfnet.coin.api;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Group20Entry;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.api.client.domain.PersonEntry;
import nl.surfnet.coin.api.oauth.Http401UnauthorizedEntryPoint;
import nl.surfnet.coin.api.oauth.JanusClientMetadata;
import nl.surfnet.coin.api.saml.SAMLAuthenticationToken;
import nl.surfnet.coin.api.service.GroupProviderAcl;
import nl.surfnet.coin.api.service.GroupService;
import nl.surfnet.coin.api.service.PersonService;
import nl.surfnet.coin.janus.domain.EntityMetadata;
import nl.surfnet.coin.shared.log.ApiCallLogService;
import nl.surfnet.coin.shared.service.ErrorMessageMailer;
import nl.surfnet.coin.teams.domain.GroupProvider;
import nl.surfnet.coin.teams.domain.GroupProviderType;
import nl.surfnet.coin.teams.domain.ServiceProviderGroupAcl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedClientException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static nl.surfnet.coin.api.GroupProviderConfiguration.Service.Group;
import static nl.surfnet.coin.api.service.GroupProviderAcl.GroupId.groupId;
import static nl.surfnet.coin.api.service.GroupProviderAcl.ServiceProviderId.spId;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class GroupZyApiControllerTest {

  public static final String SP_ENTITY_ID = "spEntityId";
  public static final String GROUP_ID = "group";
  public static final String USER_ID = "userId";
  public static final String GROUPER_GROUP_ID = "urn";
  @InjectMocks
  private ApiController controller;

  @Mock
  private PersonService personService;

  @Mock
  private GroupProviderConfiguration groupProviderConfiguration;

  @Mock
  private ErrorMessageMailer errorMessageMailer;

  @Mock
  private ApiCallLogService apiCallLogService;

  @Mock
  private GroupService groupService;

  @Mock
  private GroupProviderAcl groupProviderAcl;

  private EntityMetadata metadata;
  private PersonEntry personEntry;
  private List<GroupProvider> allGroupProviders;
  private GroupProvider grouper;
  private Group20Entry group20Entry;

  @Before
  public void setUp() throws Exception {
    controller = new ApiController();
    MockitoAnnotations.initMocks(this);
    SAMLAuthenticationToken samlAuthenticationToken = new SAMLAuthenticationToken(USER_ID, Collections.EMPTY_LIST);
    metadata = new EntityMetadata();
    metadata.setAppEntityId(SP_ENTITY_ID);
    samlAuthenticationToken.setClientMetaData(new JanusClientMetadata(metadata));
    SecurityContextHolder.getContext().setAuthentication(samlAuthenticationToken);
    personEntry = new PersonEntry();
    Person person = new Person();
    person.setId(USER_ID);
    personEntry.setEntry(person);
    grouper = new GroupProvider(1l, "id", "name", GroupProviderType.GROUPER);
    grouper.addServiceProviderGroupAcl(new ServiceProviderGroupAcl(true, true, SP_ENTITY_ID, grouper.getId()));
    allGroupProviders = Arrays.asList(grouper);
    group20Entry = new Group20Entry(Arrays.asList(new Group20(GROUP_ID, "title", "description")));

    when(personService.getPerson(USER_ID, USER_ID, metadata.getAppEntityId())).thenReturn(personEntry);
    when(groupProviderConfiguration.getAllGroupProviders()).thenReturn(allGroupProviders);
    when(groupProviderConfiguration.getAllowedGroupProviders(Group, SP_ENTITY_ID, allGroupProviders)).thenReturn(allGroupProviders);
    when(groupProviderConfiguration.isCallAllowed(Group, SP_ENTITY_ID, grouper)).thenReturn(true);
    when(groupProviderConfiguration.isInternalGroup(GROUP_ID)).thenReturn(true);
    when(groupProviderConfiguration.cutOffUrnPartForGrouper(allGroupProviders, GROUP_ID)).thenReturn(GROUPER_GROUP_ID);
    when(groupService.getGroup20(USER_ID, GROUPER_GROUP_ID, USER_ID)).thenReturn(group20Entry);
    when(groupProviderConfiguration.addUrnPartForGrouper(allGroupProviders, group20Entry)).thenReturn(group20Entry);
  }

  @Test
  public void testReturnsTheGroupsThatItHasAccessTo() throws Exception {
    when(groupProviderAcl.hasAccessTo(spId(SP_ENTITY_ID), groupId(GROUP_ID))).thenReturn(true);

    Group20Entry group = controller.getGroup(USER_ID, GROUP_ID);

    assertEquals(1, group.getEntrySize());
  }

  @Test(expected = UnauthorizedException.class)
  public void testGetGroupFailsWhenNoAccessToGroup() throws Exception {
    when(groupProviderAcl.hasAccessTo(spId(SP_ENTITY_ID), groupId(GROUP_ID))).thenReturn(false);

    controller.getGroup(USER_ID, GROUP_ID);
  }

  @Test(expected = UnauthorizedException.class)
  public void testGetGroupMembersFailsWhenNoAccessToGroup() throws Exception {
    when(groupProviderAcl.hasAccessTo(spId(SP_ENTITY_ID), groupId(GROUP_ID))).thenReturn(false);

    controller.getGroupMembers(USER_ID, GROUP_ID, 1, 0, "foo");
  }

  @Test
  public void testUnauthorizedExceptionReturns401() throws Exception {
    Group20Entry entry = controller.handleUnauthorizedException(new UnauthorizedException("foo"));
    assertEquals(0, entry.getEntrySize());
  }
}
