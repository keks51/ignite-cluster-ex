package com.keks.ex;


import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.ClientCacheConfiguration;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.ClientConfiguration;

public class JavaThinClientCacheEx {

    public static void main(String[] args) {
        ClientConfiguration cfg = new ClientConfiguration().setAddresses("127.0.0.1:10802");

        try (IgniteClient thinClient = Ignition.startClient(cfg)) {
            if (thinClient.cluster().state() != ClusterState.ACTIVE) thinClient.cluster().state(ClusterState.ACTIVE);
            // only inMemory cache
            {
                String cacheName = "java_in_memory_cache";
                ClientCacheConfiguration conf = new ClientCacheConfiguration();
                conf.setCacheMode(CacheMode.PARTITIONED);
                conf.setName(cacheName);
                ClientCache<Integer, String> cache = thinClient.getOrCreateCache(conf);
                System.out.println("Created Only InMemory Cache: " + cache.getName());
                for (int i = 0; i < 10; i++) {
                    String value = "value" + i;
                    System.out.println("Adding Key: '" + i + "'  Value: '" + value + "'");
                    cache.put(i, value);
                }
            }

            // persistence and inMemory cache
            {
                String cacheName = "java_persistence_cache";
                ClientCacheConfiguration conf = new ClientCacheConfiguration();
                conf.setCacheMode(CacheMode.PARTITIONED);
                conf.setName(cacheName);
                // for persistence. Region Should be configured in cluster config file or using Thick Client
                conf.setDataRegionName("test_persistence_region");
                ClientCache<Integer, String> cache = thinClient.getOrCreateCache(conf);
                System.out.println("Created Persistence And InMemory Cache: " + cache.getName());
                for (int i = 0; i < 10; i++) {
                    String value = "value" + i;
                    System.out.println("Adding Key: '" + i + "'  Value: '" + value + "'");
                    cache.put(i, value);
                }
                System.out.println("Cache: " + cacheName + "  Size: " + cache.size());
            }
        }
    }

}
