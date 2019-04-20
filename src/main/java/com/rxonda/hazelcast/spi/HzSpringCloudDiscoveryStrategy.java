package com.rxonda.hazelcast.spi;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rxonda.hazelcast.spi.consul.HzSpringCloudConsulDiscoveryStrategyProperties.*;

@Slf4j
public abstract class HzSpringCloudDiscoveryStrategy extends AbstractDiscoveryStrategy {
    protected DiscoveryClient discoveryClient;
    protected ServiceRegistry<Registration> serviceRegistry;
    protected DiscoveryNode discoveryNode;
    protected Registration registration;
    protected String applicationScope;

    public HzSpringCloudDiscoveryStrategy(DiscoveryClient discoveryClient, ServiceRegistry<Registration> serviceRegistry,
                                          DiscoveryNode discoveryNode, ILogger logger,
                                          Map<String, Comparable> properties) {
        super(logger, properties);
        this.discoveryClient = discoveryClient;
        this.serviceRegistry = serviceRegistry;
        this.discoveryNode = discoveryNode;
        boolean preferPublic = getOrDefault(PREFER_PUBLIC_IP, false);
        int discoveryDelay = getOrDefault(DISCOVERY_DELAY, 10_000);
        int biPort = getOrDefault(BI_PORT, 8081);
        String checkInterval = getOrDefault(CHECK_INTERVAL, "30s");
        this.applicationScope = getOrDefault(APPLICATION_SCOPE, "hazelcast");
        List<String> tags = Arrays.stream(getOrDefault(TAGS, "hazelcast").split(","))
                .collect(Collectors.toList());
        String healthCheckUrl = getOrDefault(HEALTHCHECK_URL, "/health");
        log.info("Aplication Scope: " + this.applicationScope);
        log.info("Aplication Tags: "+ tags);
        log.info("Prefer public IP: " + preferPublic);
        log.info("Discovery delay: " + discoveryDelay);
        log.info("Check interval: " + checkInterval);
        log.info("Hazelcast health check URL: " + healthCheckUrl);

        this.registration = buildRegistration(tags, preferPublic, discoveryDelay, biPort, healthCheckUrl, checkInterval);
    }

    protected abstract Registration buildRegistration(List<String> tags, boolean preferPublic, int discoveryDelay, int biPort, String healthCheckUrl, String checkInterval);

    @Override
    public void start() {
        serviceRegistry.register(this.registration);
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        return discoveryClient.getInstances(applicationScope).stream()
                .map( HzSpringCloudDiscoveryStrategy::mapEndpoint )
                .collect(Collectors.toList());
    }

    @Override
    public void destroy() {
        serviceRegistry.deregister(registration);
    }

    private static DiscoveryNode mapEndpoint(ServiceInstance serviceInstance) {
        try {
            String host = serviceInstance.getHost();
            int port = serviceInstance.getPort();
            return new SimpleDiscoveryNode(new Address(host, port));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
