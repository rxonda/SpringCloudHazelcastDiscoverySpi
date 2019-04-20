package com.rxonda.hazelcast.spi.consul;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(value = {DiscoveryClient.class, ServiceRegistry.class})
@ConditionalOnProperty(value = "spring.cloud.consul.enabled")
public class HzSpringCloudConsulDiscoveryStrategyConfiguration {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private ServiceRegistry<Registration> serviceRegistry;

    @Bean(name = "hzDiscoveryStrategyFactory")
    @ConditionalOnMissingBean
    public HzSpringCloudConsulDiscoveryStrategyFactory hzDiscoveryStrategyFactory() {
        return new HzSpringCloudConsulDiscoveryStrategyFactory(discoveryClient, serviceRegistry);
    }
}
