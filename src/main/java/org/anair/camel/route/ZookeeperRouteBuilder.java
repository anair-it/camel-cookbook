package org.anair.camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.zookeeper.policy.ZooKeeperRoutePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Zookeeper recipes:
 * <ul>
 * <li>Leader election recipe - Process MQ messages only one node in a clustered application</li>
 * <li>Group membership - Register nodes in a cluster definition</li>
 * <li>Group membership - List nodes in a cluster</li>
 * </ul>
 * 
 * @see <a href="http://camel.apache.org/zookeeper.html">Zookeeper component</a>
 * @author Anoop Nair
 *
 */
@Component
public class ZookeeperRouteBuilder extends RouteBuilder{
	
	@Value("${environment.mq.queues.dlq}")
	protected String DLQ;
	@Value("${environment.mq.queues.camel-in-queue}")
	protected String camelInQueue;
	@Value("${environment.mq.queues.camel-in-queue}")
	protected String inQueue;
	@Value("${environment.zookeeper.ensemble}")
	private String zookeeperConnectString;
	@Value("${environment.zookeeper.namespace}")
	private String namespace;

	@Autowired
	private String hostName;
	@Autowired
	private ZooKeeperRoutePolicy zookeeperRoutePolicy;
	
	@Override
	public void configure() throws Exception {
		errorHandler(deadLetterChannel("mq:"+DLQ)
				.useOriginalMessage()
				.logHandled(true));
		
		fromF("timer:registerNode?repeatCount=1&delay=5000").routeId("zoo-init")
			.routeDescription("On startup, register this node in the cluster. The ZNode will be deleted if the app node goes down.")
			.toF("zoo:%s/app/cluster/%s?create=true", zookeeperConnectString+namespace, hostName)
		.end();
		
		fromF("mq:%s",camelInQueue).routeId("mq-in")
			.toF("direct:zin")
		.end();
		
		fromF("zoo:%s/app/cluster?listChildren=true", zookeeperConnectString+namespace).routeId("list-children")
			.routeDescription("Invoke this on demand to identify all nodes in a cluster.")
			.log("Children: ${body}")
		.end();
				
		fromF("direct:zin").routeId("leader")
			.routeDescription("Process message only on one/leader node")
			.routePolicy(zookeeperRoutePolicy)
			.log("I'm the leader node. Message: ${body}")
		.end();
	}
}
