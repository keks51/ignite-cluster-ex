package com.keks.ex

import org.apache.ignite.Ignition
import org.apache.ignite.client.IgniteClient
import org.apache.ignite.cluster.ClusterState
import org.apache.ignite.configuration.ClientConfiguration

import scala.jdk.CollectionConverters._


object ThinClientConnEx {

  def main(args: Array[String]): Unit = {

    val cfg = new ClientConfiguration().setAddresses("127.0.0.1:10802")
    val thinClient: IgniteClient = Ignition.startClient(cfg)
    if (thinClient.cluster().state() != ClusterState.ACTIVE) thinClient.cluster().state(ClusterState.ACTIVE)

    val nodes = thinClient.cluster().nodes().asScala.map { node =>
      (node.hostNames().asScala.head, node.addresses().asScala.head)
    }
    println(
      s"""Nodes in Cluster:
         |${nodes.map(e => s"HostName: " + e._1 + "   Ip: " + e._2).mkString("\n")}
         |""".stripMargin)

    thinClient.close()
  }

}
