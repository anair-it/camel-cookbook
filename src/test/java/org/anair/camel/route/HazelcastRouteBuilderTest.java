package org.anair.camel.route;

import org.anair.spring.config.HazelcastConfiguration;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.hazelcast.HazelcastConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * Testing Hazelcast scenarios.
 * 
 * @author Anoop Nair
 *
 */
@RunWith(CamelSpringBootRunner.class)
@EnableAutoConfiguration
@ContextConfiguration(classes={HazelcastConfiguration.class})
@TestPropertySource(locations={"classpath:application-test.properties"})
public class HazelcastRouteBuilderTest {

	@EndpointInject(uri = "mock:out")
	private MockEndpoint mockOutEndpoint;
	
	@Produce
	private ProducerTemplate hzProducer;
	
	@Autowired
	private ModelCamelContext context;

	@Test
	public void hazelcast_put() throws Exception {
		context.getRouteDefinition("direct-hz-put").adviceWith(context, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint("hz:"+HazelcastConstants.MAP_PREFIX+"cart")
				.skipSendToOriginalEndpoint()
				.to("mock:out");
				
			}
		});
		context.start();
		
		
		//Put element in cache with key as "key1" and element as "data1"
		mockOutEndpoint.expectedMessageCount(1);
		hzProducer.sendBodyAndHeader("direct:put", "data1", HazelcastConstants.OBJECT_ID, "key1");
		mockOutEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void hazelcast_putAndGet() throws Exception {
		hzProducer.sendBodyAndHeader("direct:put", "data1", HazelcastConstants.OBJECT_ID, "key1");
		
		//Get element from cache with key as "key1". Expected to get back "data1"
		mockOutEndpoint.expectedBodiesReceived("data1");
		mockOutEndpoint.expectedMessageCount(1);
	}
	
}
