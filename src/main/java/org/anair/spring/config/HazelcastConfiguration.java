package org.anair.spring.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.anair.camel.route.HazelcastRouteBuilder;
import org.apache.camel.component.hazelcast.HazelcastComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
@Import(value={HazelcastRouteBuilder.class})
public class HazelcastConfiguration {
	
	@Value("${environment.hazelcast.name}")
	private String groupName;
	@Value("${environment.hazelcast.cluster}")
	private String cluster;
	@Value("${environment.hazelcast.port}")
	private Integer port;
	
	
	@Bean
	public HazelcastComponent hz(){
		HazelcastComponent hzComponent = new HazelcastComponent();
		hzComponent.setHazelcastInstance(hazelcast());
		return hzComponent;
	}
	
	@Bean
	public HazelcastInstance  hazelcast() {
		Config config = new Config("hazelcast");
		config.setProperty("hazelcast.logging.type", "slf4j");
		config.setGroupConfig(new GroupConfig(groupName));
		NetworkConfig nc = new NetworkConfig();
		JoinConfig jc = new JoinConfig();
		TcpIpConfig tcp = new TcpIpConfig();
		tcp.setMembers(Arrays.asList(cluster.split(",")));
		tcp.setEnabled(true);
		jc.setTcpIpConfig(tcp);
		MulticastConfig mc = new MulticastConfig();
		mc.setEnabled(false);
		jc.setMulticastConfig(mc);
		nc.setJoin(jc);
		nc.setPort(port);
		config.setNetworkConfig(nc);
		
		Map<String,MapConfig> mapConfigs = new HashMap<>();
		MapConfig cartMapConfig = new MapConfig("cart");
		cartMapConfig.setAsyncBackupCount(1);
		cartMapConfig.setEvictionPolicy(EvictionPolicy.LRU);
		cartMapConfig.setMergePolicy("com.hazelcast.map.merge.PassThroughMergePolicy");
		cartMapConfig.setStatisticsEnabled(true);
		cartMapConfig.setTimeToLiveSeconds(10000);
		
		mapConfigs.put("cart", cartMapConfig);
		
		config.setMapConfigs(mapConfigs);
		
		return Hazelcast.newHazelcastInstance(config);
	}

}
