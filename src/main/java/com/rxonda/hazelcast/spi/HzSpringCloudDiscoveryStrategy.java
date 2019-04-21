package com.rxonda.hazelcast.spi;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.stream.Collectors;

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
        this.registration = buildRegistration();
    }

    protected abstract Registration buildRegistration();

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
