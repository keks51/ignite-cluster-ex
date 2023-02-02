package com.keks.ex;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteSystemProperties;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import com.keks.ex.model.Person;
import com.keks.ex.model.PersonKey;

import java.util.Collections;
import java.util.List;


public class JavaThickClientSqlWebConsoleEx {

    public static void main(String[] args) {
        //  you can query data using web-console
        // for this example: select * from CACHE_SCHEMA.PERSON;

        System.setProperty(IgniteSystemProperties.IGNITE_QUIET, "true"); // verbose logging false

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true);
        cfg.setPeerClassLoadingEnabled(true);

        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Collections.singleton("127.0.0.1:47501"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        String CACHE_NAME = "person-cache-v1";
        String CACHE_SCHEMA = "JAVA_CACHE_SCHEMA"; // defined in docker-ignite.xml
        try (Ignite thickClient = Ignition.start(cfg)) {
            if (thickClient.cluster().state() != ClusterState.ACTIVE) thickClient.cluster().state(ClusterState.ACTIVE);

            CacheConfiguration<PersonKey, Person> ccfg = new CacheConfiguration<PersonKey, Person>(CACHE_NAME)
                    .setIndexedTypes(PersonKey.class, Person.class)
                    .setSqlSchema(CACHE_SCHEMA);
            IgniteCache<PersonKey, Person> cache = thickClient.getOrCreateCache(ccfg);

            // putting
            for (int i = 0; i < 1_000; i++) {
                cache.put(new PersonKey(i), new Person(i, "alex" + i, i, 1.0f));
            }

            // querying. ResultSet contains only fields id and name.
            FieldsQueryCursor<List<?>> cursor = cache.query(new SqlFieldsQuery("SELECT * FROM Person limit 10"));
            for (List<?> row : cursor) {
                System.out.println(row);
            }
        }
    }

}
