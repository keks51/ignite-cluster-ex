#!/bin/bash

# vars COMMUNICATION_PORT_VAR, DISCOVERY_PORT_VAR and CLIENT_PORT_VAR are not set in source config 'docker-ignite.xml'
# this is done because there is no solution to set specific port value via ENV variables while starting node and if
# we left the default port value then each node has the same port (for ex 45000)
# but each node should have different port.
# Then starting ignite
conf_path=$(echo "$CONFIG_URI" | sed "s/file:\/\///")
parameterized_conf_path="/opt/ignite/apache-ignite/config/docker-ignite-parameterized.xml"
cp "$conf_path" $parameterized_conf_path
CONFIG_URI="file://$parameterized_conf_path"
sed -i 's/COMMUNICATION_PORT_VAR/'"$COMMUNICATION_PORT_VAR"'/' $parameterized_conf_path
sed -i 's/DISCOVERY_PORT_VAR/'"$DISCOVERY_PORT_VAR"'/' $parameterized_conf_path
sed -i 's/CLIENT_PORT_VAR/'"$CLIENT_PORT_VAR"'/' $parameterized_conf_path
echo "$DISCOVERY_PORT_VAR"
echo "$COMMUNICATION_PORT_VAR"
echo "$CLIENT_PORT_VAR"
"$IGNITE_HOME"/run.sh
