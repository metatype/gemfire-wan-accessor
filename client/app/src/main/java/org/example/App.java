package org.example;

import java.util.HashSet;
import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

public class App {
  public static void main(String[] args) throws InterruptedException {
    System.out.println("connecting to cluster");
    ClientCache clientCache = new ClientCacheFactory()
      .addPoolLocator("localhost", 10334)
      .create();
    
    Region<String, String> region = clientCache.<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY).create("example-region");

    System.out.println("inserting keys into region");
    region.put("key1", "value1");
    region.put("key2", "value2");
    region.put("key3", "value3");

    dumpKeys(region);

    System.out.println("removing keys from region");
    region.remove("key1");
    // region.removeAll(Set.of("key1", "key2"));
    Set<String> keys = new HashSet<>();
    // keys.add("key1");
    keys.add("key2");
    region.removeAll(keys);
    dumpKeys(region);

    System.out.println("closing connection to cluster");
    clientCache.close();

    System.out.println("connecting to wan destination cluster");
    clientCache = new ClientCacheFactory()
        .addPoolLocator("localhost", 10335)
        .create();
      
    region = clientCache.<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY).create("example-region");

    Thread.sleep(5000);
    dumpKeys(region);

    clientCache.close();
  }

  public static void dumpKeys(Region<String, String> region) {
    System.out.println("getting keys from region");
    System.out.println("key1: " + region.get("key1"));
    System.out.println("key2: " + region.get("key2"));
    System.out.println("key3: " + region.get("key3"));
  }
}
