package nl.surfnet.coin.api.service;

public interface GroupProviderAcl {
  boolean hasAccessTo(ServiceProviderId id, GroupId groupId);

  public static class GroupId {
    public final String id;

    private GroupId(String id) {
      this.id = id;
    }

    public static GroupId groupId(String id) {
      return new GroupId(id);
    }
  }

  public static class ServiceProviderId {
    public final String id;

    private ServiceProviderId(String id) {
      this.id = id;
    }

    public static ServiceProviderId spId(String id) {
      return new ServiceProviderId(id);
    }

  }

}
