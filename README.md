# Example of creating ignite cluster using docker compose and connecting via thick and thin clients from Intellij Idea

## Steps for Mac users:
1) docker compose up -d
2) Add loopback for ips:  
   sudo ifconfig lo0 alias 10.5.0.2/28
   sudo ifconfig lo0 alias 10.5.0.3/28
   sudo ifconfig lo0 alias 10.5.0.4/28
   sudo ifconfig lo0 alias 10.5.0.5/28
   sudo ifconfig lo0 alias 10.5.0.6/28
   sudo ifconfig lo0 alias 10.5.0.7/28
   sudo ifconfig lo0 alias 10.5.0.8/28
   sudo ifconfig lo0 alias 10.5.0.9/28
   sudo ifconfig lo0 alias 10.5.0.10/28
   sudo ifconfig lo0 alias 10.5.0.11/28
   sudo ifconfig lo0 alias 10.5.0.12/28
   sudo ifconfig lo0 alias 10.5.0.13/28
   sudo ifconfig lo0 alias 10.5.0.14/28
This step should be repeated after each machine restart.   
To unbound sudo ifconfig lo0 -alias 10.5.0.6/29
3) Run examples


## Explanation
![Alt text](/png/ignite-cluster.png)
1) Connecting to first ignite node by localhost:47501.   
 Discovery port is open, forwarded and reachable from host
2) Response from ignite goes through gateway and returns back to Thick client   
3) Thick client receives cluster topology but doesn't know anything about ips 10.5.0.2    
and 10.5.0.3 since these ips are reachable only inside IGNITE-CLUSTER-NETWORK   
4) Topology ips are mapped to lo0 (Loopback) which means localhost and requests 
are forwarded to opened ports

## Ignite Node Container configuration
One container -> One Ignite Node.  
That means in default conf each node uses the same ports  
ignite-01:   
47100,47100   
47500,47500   
10800,10800      
ignite-02:  
47101,47100  
47501,47500  
10801,10800  
But in our case we should have an ability to communicate with each node and      
if all nodes uses the same ports client cannot start since all request goes only to first node.    
For example:   
1) Ignite-01 response to Thick client via internal port 47100 and external 47100     
2) Response contains information  about all nodes in cluster including node's ports   
3) Client tries to connect to Ignite-02 using topology information    
in which ignite-02 uses 47100 ports (But Container mapped as 47101:47100)   
4) Client connecting to ignite-01 (since first container mapped as 47100:47100) instead of ignite-02 (47101:47100) and fails   
To get around this issue all nodes should use different ports but this could not be set via ENV variables.   
Only using conf file. That's why cong file contains DISCOVERY_PORT_VAR,COMMUNICATION_PORT_VAR and CLIENT_PORT_VAR  
instead of real values. In compose yml for each node env variables are set and during node start up each container   
copies conf file and set ports. (See file ../docker/config/start_node.sh)



## WEB CONSOLE
1) generate ssl keys inside ./docker/web_console_config/ using  
'openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout server.nopass.key -out server.crt'    
type any country code during creation   
   If you enter '.', the field will be left blank.    
   -----    
   Country Name (2 letter code) []:AU  
2) uncomment lines after WEB CONSOLE 
3) docker-compose up -d
4) go to https://localhost:80/
5) sing up
6) go to user_name/profile
7) copy security token
8) replace TOKEN_REPLACE_ME with copied token in 2 places
9) docker-compose up -d
10) go back to https://localhost:80/ and you should see 'MyConnectedClusters:1'
11) now you can run 'ThickClientSqlWebConsoleEx or JavaThickClientSqlWebConsoleEx' examples and query results using web-console 