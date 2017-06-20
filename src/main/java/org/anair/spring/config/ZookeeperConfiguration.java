package org.anair.spring.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import org.anair.camel.route.ZookeeperRouteBuilder;
import org.apache.camel.component.zookeeper.ZooKeeperComponent;
import org.apache.camel.component.zookeeper.ZooKeeperConfiguration;
import org.apache.camel.component.zookeeper.policy.ZooKeeperRoutePolicy;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={ZookeeperRouteBuilder.class})
public class ZookeeperConfiguration {
	
	@Value("${environment.zookeeper.ensemble}")
	private String connectionString;
	@Value("${environment.zookeeper.namespace}")
	private String namespace;
	@Value("${environment.zookeeper.leader-path}")
	private String leaderPath;
	
	private Random randomNumberGenerator = new Random();

	@Bean
	public ZooKeeperComponent zoo() {
		ZooKeeperConfiguration zooKeeperConfiguration = new ZooKeeperConfiguration();
		zooKeeperConfiguration.setCreateMode(CreateMode.EPHEMERAL.name());
		
		ZooKeeperComponent zooKeeperComponent = new ZooKeeperComponent(zooKeeperConfiguration);
		
		return zooKeeperComponent;
	}

	@Bean
	public ZooKeeperRoutePolicy zookeeperRoutePolicy() {
		return new ZooKeeperRoutePolicy("zoo://"+connectionString+namespace+leaderPath, 1);
	}
	
	@Bean
	public String hostName() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostName()+randomNumberGenerator.nextInt(10);
	}
}
