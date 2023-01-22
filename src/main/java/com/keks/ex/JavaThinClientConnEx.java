package com.keks.ex;

import org.apache.ignite.Ignition;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.ClientConfiguration;

import java.util.Collection;


public class JavaThinClientConnEx {

    public static void main(String[] args) {
        ClientConfiguration cfg = new ClientConfiguration().setAddresses("127.0.0.1:10802");

        try (IgniteClient thinClient = Ignition.startClient(cfg)) {
            if (thinClient.cluster().state() != ClusterState.ACTIVE) thinClient.cluster().state(ClusterState.ACTIVE);

            System.out.println("Nodes in Cluster:");

            Collection<ClusterNode> nodes = thinClient.cluster().nodes();
            for (ClusterNode node : nodes) {
                System.out.println("HostName: " + node.hostNames().toArray()[0] + "   Ip: " + node.addresses().toArray()[0]);
            }
        }
    }

}
