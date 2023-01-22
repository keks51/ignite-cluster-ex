package com.keks.ex

import com.keks.ex.IgniteUtils.withIgniteThinClient
import org.apache.ignite.cache.CacheMode
import org.apache.ignite.client.{ClientCache, ClientCacheConfiguration}


object ThinClientCacheEx {

  def main(args: Array[String]): Unit = withIgniteThinClient("127.0.0.1", 10802) { implicit thinClient =>
    // only inMemory cache
    {
      val cacheName = "in_memory_cache"
      val conf = new ClientCacheConfiguration()
      conf.setCacheMode(CacheMode.PARTITIONED)
      conf.setName(cacheName)
      val cache: ClientCache[Int, String] = thinClient.getOrCreateCache[Int, String](conf)
      println("Created Only InMemory Cache: " + cache.getName)
      (0 to 10).foreach { i =>
        val value = s"value$i"
        println(s"Adding Key: '$i'  Value: '$value'")
        cache.put(i, value)
      }

      println(s"Cache: $cacheName  Size: ${cache.size()}")

    }

    // persistence and inMemory cache
    {
      val cacheName = "persistence_cache"
      val conf = new ClientCacheConfiguration()
      conf.setCacheMode(CacheMode.PARTITIONED)
      // for persistence. Region Should be configured in cluster config file or using Thick Client
      conf.setDataRegionName("test_persistence_region")
      conf.setName(cacheName)
      val cache: ClientCache[Int, String] = thinClient.getOrCreateCache[Int, String](conf)
      println("Created Persistence And InMemory Cache: " + cache.getName)
      (0 to 10).foreach { i =>
        val value = s"value$i"
        println(s"Adding Key: '$i'  Value: '$value''")
        cache.put(i, value)
      }

      println(s"Cache: $cacheName  Size: ${cache.size()}")

    }


  }


}
