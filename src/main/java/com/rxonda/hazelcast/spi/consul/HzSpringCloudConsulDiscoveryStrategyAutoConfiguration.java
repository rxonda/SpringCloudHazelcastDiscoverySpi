package com.rxonda.hazelcast.spi.consul;

import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClientConfiguration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AutoConfigureAfter(value = { ConsulServiceRegistryAutoConfiguration.class, ConsulDiscoveryClientConfiguration.class })
@ConditionalOnBean(value = {DiscoveryClient.class, ServiceRegistry.class})
@ConditionalOnProperty(value = "spring.cloud.consul.enabled")
public class HzSpringCloudConsulDiscoveryStrategyAutoConfiguration {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Bean(name = "discoveryStrategyFactory")
    @ConditionalOnMissingBean
    public DiscoveryStrategyFactory hzDiscoveryStrategyFactory() {
        return new HzSpringCloudConsulDiscoveryStrategyFactory(discoveryClient, serviceRegistry);
    }

    @Bean(name = "discoveryStrategyConfig")
    public DiscoveryStrategyConfig hzDiscoveryStrategyConfig(Environment env, DiscoveryStrategyFactory discoveryStrategyFactory) {
        Map<String, Comparable> props = new HashMap<>();
        props.put("consul.name", env.getProperty("hazelcast.discovery.consul.group-name"));
        props.put("consul.tags", env.getProperty("hazelcast.discovery.consul.tags"));
        props.put("consul.discovery-delay", env.getProperty("hazelcast.discovery.consul.discovery-delay","10000"));
        props.put("consul.check-interval", env.getProperty("hazelcast.discovery.consul.check-interval","30s"));
        props.put("consul.prefer-public", env.getProperty("hazelcast.discovery.consul.prefer-public", "false"));
        return  new DiscoveryStrategyConfig(discoveryStrategyFactory, props);
    }
}
