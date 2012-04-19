package nl.surfnet.coin.api.service;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;

/**
 * 
 * Interface for GroupProviders that can be configured
 * 
 */
public interface ConfigurableGroupProvider {

  void addPerson(Person person);

  void addGroup(Group20 group);
  
  void addPersonToGroup(String personId, String groupId);
  
  void reset();
  
  void sleep(long millSeconds);
  
}
