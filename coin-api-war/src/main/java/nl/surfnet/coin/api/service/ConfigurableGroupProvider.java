package nl.surfnet.coin.api.service;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;

/**
 * 
 * Interface for GroupProviders that can be configured
 * 
 */
public interface ConfigurableGroupProvider {

  public void addPerson(Person person);

  public void addGroup(Group20 group);
  
  public void addPersonToGroup(String personId, String groupId);
  
  public void reset();
  
  public void sleep(long millSeconds);
  
}
