package org.anair.camel.route;

import java.nio.charset.Charset;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hazelcast.HazelcastConstants;
import org.springframework.stereotype.Component;
import org.xerial.snappy.Snappy;

/**
 * Hazelcast distributed cache - camel recipe.
 * 
 * <p>This is an example of storing compressed data in cache. Compression is through Google snappy codec.
 * <p>Testcase: {@link HazelcastRouteBuilderTest} 
 * 
 * @see <a href="http://camel.apache.org/hazelcast-component.html">Hazelcast component</a>
 * @see <a href="http://google.github.io/snappy/">Google Snappy</a>
 * 
 * @author Anoop Nair
 *
 */
@Component
public class HazelcastRouteBuilder extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		from("direct:put").routeId("direct-put")
			.routeDescription("Compress data using snappy before putting to cache")
			.process(new Processor() {
				
				@Override
				public void process(Exchange exchange) throws Exception {
					String payload = exchange.getIn().getBody(String.class);
					byte[] compressedBytes = Snappy.compress(payload.getBytes(Charset.forName("UTF-8")));
					exchange.getIn().setBody(compressedBytes);
				}
			})
		.to("direct:hz-put");
		
		from("direct:hz-put").routeId("direct-hz-put")
			.routeDescription("Put compressed data in cache with the key provided in the header")
			.setHeader(HazelcastConstants.OPERATION, constant(HazelcastConstants.PUT_OPERATION))
			.toF("hz:%scart", HazelcastConstants.MAP_PREFIX)
		.end();
		
		from("direct:get")
			.setHeader(HazelcastConstants.OPERATION, constant(HazelcastConstants.GET_OPERATION))
			.toF("hz:%scart", HazelcastConstants.MAP_PREFIX)
			.process(new Processor() {
					
				@Override
				public void process(Exchange exchange) throws Exception {
					byte[] payloadBytes = exchange.getIn().getBody(byte[].class);
					byte[] uncompressedBytes = Snappy.uncompress(payloadBytes);
					String string = new String(uncompressedBytes, Charset.forName("UTF-8"));
					exchange.getIn().setBody(string);
				}
			})
			.to("mock:out")
		.end();
	}
}
