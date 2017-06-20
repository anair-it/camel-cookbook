# Apache Camel cookbook
This project is a compilation of Apache Camel recipes with supporting camel unit tests with spring boot enabled runtime environment


Software pre-requisite
--------
1. JDK 8+
2. Maven 3+
3. Camel 2.19.x
4. IBM Websphere MQ 7.5 server and client jars
5. Spring boot 1.5.x
6. Zookeeper >= 3.4.6          


Setting up your environment
----
1. Create a local queue manager. Name it as you wish.   
2. Create queues _CAMEL.IN_, _CAMEL.OUT_ and _CAMEL.DLQ_     
3. Start queue manager    
4. Create a zookeeper cluster local or remote  
5. Start zookeeper cluster
6. Update _src/main/resources/application.yml_ with queue manager, queue names and zookeeper connection info  


## Recipes

### Zookeeper leader election

This recipe covers zookeeper and IBM MQ usage in camel.

1. Open a terminal and run `mvn clean package` and `java -jar target/camel-cookbook.jar`
2. Open multiple terminals and run `java -jar target/camel-cookbook.jar` to create a cluster connected to a zookeeper ensemble 
2. Put multiple messages in CAMEL.IN    
3. Check logs to make sure only node is processing the message
4. Shutdown one of the camel-cookbook instance
5. Drop a message again and verify that another node has claimed leadership and processed the message


### Hazelcast caching
The recipe covers putting a snappy compressed data into cache and then retrieving it later. Refer unit tests.