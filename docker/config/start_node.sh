#!/bin/bash

# vars COMMUNICATION_PORT_VAR, DISCOVERY_PORT_VAR and CLIENT_PORT_VAR are not set in source config 'docker-ignite.xml'
# this is done because there is no solution to set specific port value via ENV variables while starting node and if
# we left the default port value then each node has the same port (for ex 45000)
# but each node should have different port
# and then starting ignite
conf_path=$(echo "$CONFIG_URI" | sed "s/file:\/\///")
cp "$conf_path" /docker-ignite.xml
CONFIG_URI="file:///docker-ignite.xml"
sed -i 's/COMMUNICATION_PORT_VAR/'"$COMMUNICATION_PORT_VAR"'/' /docker-ignite.xml
sed -i 's/DISCOVERY_PORT_VAR/'"$DISCOVERY_PORT_VAR"'/' /docker-ignite.xml
sed -i 's/CLIENT_PORT_VAR/'"$CLIENT_PORT_VAR"'/' /docker-ignite.xml
echo "$DISCOVERY_PORT_VAR"
echo "$COMMUNICATION_PORT_VAR"
echo "$CLIENT_PORT_VAR"
"$IGNITE_HOME"/run.sh
