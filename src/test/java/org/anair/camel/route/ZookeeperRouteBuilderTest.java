package org.anair.camel.route;

import org.anair.camel.route.ZookeeperRouteBuilder;
import org.anair.spring.config.ZookeeperConfiguration;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;



/**
 * Testing Zookeeper leader election scenario.
 * 
 * {@link ZookeeperRouteBuilder}
 * 
 * @author Anoop Nair
 *
 */
@ContextConfiguration(classes={ZookeeperConfiguration.class})
@Ignore
public class ZookeeperRouteBuilderTest extends CamelCookbookBaseTest {

	@EndpointInject(uri = "mock:out1")
	private MockEndpoint outQueueEndpoint;
	
	@Produce
	private ProducerTemplate zookeeperProducer;
	
	@Test
	public void leaderElection() throws InterruptedException {
		outQueueEndpoint.expectedMessageCount(1);
		outQueueEndpoint.expectedBodiesReceived("message1");
		
		zookeeperProducer.sendBody("direct:zin", "message1");
		
		outQueueEndpoint.assertIsSatisfied();
		
	}
	
	@Test
	public void listChildrenZNodes() throws InterruptedException {
		zookeeperProducer.sendBody("zoo:zoonode1,zoonode2,zoonode3/camel-zoo/app/cluster?listChildren=true", "");
	}
}
