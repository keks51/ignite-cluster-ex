package com.keks.ex

import org.apache.ignite.cluster.ClusterState
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import org.apache.ignite.{IgniteSystemProperties, Ignition}

import scala.jdk.CollectionConverters._


object ThickClientConnEx {

  def main(args: Array[String]): Unit = {

    System.setProperty(IgniteSystemProperties.IGNITE_QUIET, "true") // verbose logging false

    val cfg = new IgniteConfiguration()
    cfg.setClientMode(true)
    cfg.setPeerClassLoadingEnabled(true)

    val ipFinder = new TcpDiscoveryVmIpFinder()
    val nodesAddresses = Seq("127.0.0.1:47501") //  node's ip from docker network
    ipFinder.setAddresses(nodesAddresses.asJava)
    cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder))

    val thickClient = Ignition.start(cfg)
    if (thickClient.cluster().state() != ClusterState.ACTIVE) thickClient.cluster().state(ClusterState.ACTIVE)

    val nodes = thickClient.cluster().nodes().asScala.map { node =>
      (node.hostNames().asScala.head, node.addresses().asScala.head)
    }
    println(
      s"""Nodes in Cluster:
         |${nodes.map(e => s"HostName: " + e._1 + "   Ip: " + e._2).mkString("\n")}
         |""".stripMargin)

    thickClient.close()

  }

}
