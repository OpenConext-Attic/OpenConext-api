package nl.surfnet.coin.api.service;

/**
 * Default implementation within openconext context. There is
 * no group provider acl for serviceproviders so access is always true.
 */
public class AlwaysInGroupProviderAcl implements GroupProviderAcl {
  @Override
  public boolean hasAccessTo(ServiceProviderId id, GroupId groupId) {
    return true;
  }
}
