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

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      GroupId groupId = (GroupId) o;

      if (!id.equals(groupId.id)) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return id.hashCode();
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

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ServiceProviderId that = (ServiceProviderId) o;

      if (!id.equals(that.id)) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }
  }

}
