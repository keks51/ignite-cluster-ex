package com.keks.ex;

import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.IgniteSystemProperties;
import org.apache.ignite.Ignition;

import java.util.Collection;
import java.util.Collections;

public class JavaThickClientConnEx {

    public static void main(String[] args) {
        System.setProperty(IgniteSystemProperties.IGNITE_QUIET, "true"); // verbose logging false

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true);
        cfg.setPeerClassLoadingEnabled(true);

        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Collections.singleton("127.0.0.1:47501"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        try (Ignite thickClient = Ignition.start(cfg)) {
            if (thickClient.cluster().state() != ClusterState.ACTIVE) thickClient.cluster().state(ClusterState.ACTIVE);

            System.out.println("Nodes in Cluster:");

            Collection<ClusterNode> nodes = thickClient.cluster().nodes();
            for (ClusterNode node : nodes) {
                System.out.println("HostName: " + node.hostNames().toArray()[0] + "   Ip: " + node.addresses().toArray()[0]);
            }

        }
    }

}
