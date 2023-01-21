package com.keks.ex

import org.apache.ignite.cache.CacheMode
import org.apache.ignite.client.{ClientCache, ClientCacheConfiguration, IgniteClient}
import org.apache.ignite.cluster.ClusterState
import org.apache.ignite.configuration.{ClientConfiguration, IgniteConfiguration}
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import org.apache.ignite.{Ignite, IgniteSystemProperties, Ignition}

import scala.jdk.CollectionConverters._
import scala.util.Using


object IgniteUtils {

  def withIgniteThinClient(host: String, port: Int)(func: IgniteClient => Unit): Unit = {
    val cfg = new ClientConfiguration().setAddresses(s"$host:$port")
    Using(Ignition.startClient(cfg)) { client =>
      if (client.cluster().state() != ClusterState.ACTIVE) client.cluster().state(ClusterState.ACTIVE)
      func(client)
    }
      .failed.foreach(throw _)
  }

  def withIgniteThickClient(host: String, port: Int)(func: Ignite => Unit): Unit = {
    System.setProperty(IgniteSystemProperties.IGNITE_QUIET, "true") // verbose logging false

    val cfg = new IgniteConfiguration()
    cfg.setClientMode(true)
    cfg.setPeerClassLoadingEnabled(true);

    val ipFinder = new TcpDiscoveryVmIpFinder()
    val nodesAddresses = Seq(s"$host:$port")
    ipFinder.setAddresses(nodesAddresses.asJava)
    cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

    Using(Ignition.start(cfg)) { client =>
      if (client.cluster().state() != ClusterState.ACTIVE) client.cluster().state(ClusterState.ACTIVE)
      func(client)
    }
      .failed.foreach(throw _)
  }

  def createPersistenceCache[K, V](cacheName: String)(implicit client: IgniteClient): ClientCache[K, V] = {
    val conf = new ClientCacheConfiguration()
    conf.setCacheMode(CacheMode.PARTITIONED)
    conf.setDataRegionName("test_persistence_region")
    conf.setName(cacheName)
    client.getOrCreateCache(conf)
  }

  def createInMemoryCache[K, V](cacheName: String)(implicit client: IgniteClient): ClientCache[K, V] = {
    val conf = new ClientCacheConfiguration()
    conf.setCacheMode(CacheMode.PARTITIONED)
    conf.setDataRegionName("in_memory_region")
    conf.setName(cacheName)

    client.getOrCreateCache(conf)
  }

  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block // call-by-name
    val t1 = System.nanoTime()
    println("Elapsed time: " + ((t1 - t0) / 1000 / 1000 / 1000) + "sec")
    result
  }

}
