package com.rxonda.hazelcast.spi.consul;

import com.ecwid.consul.v1.agent.model.NewService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.rxonda.hazelcast.spi.HzSpringCloudDiscoveryStrategy;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rxonda.hazelcast.spi.consul.HzSpringCloudConsulDiscoveryStrategyProperties.*;

public class HzSpringCloudConsulDiscoveryStrategy extends HzSpringCloudDiscoveryStrategy {

    public HzSpringCloudConsulDiscoveryStrategy(DiscoveryClient discoveryClient, ServiceRegistry<Registration> serviceRegistry,
                                                DiscoveryNode discoveryNode, ILogger logger,
                                                Map<String, Comparable> properties) {
        super(discoveryClient, serviceRegistry, discoveryNode, logger, properties);
    }

    @Override
    protected Registration buildRegistration() {
        getLogger().info("Starting Hazelcast Consul Discovery Strategy...");

        boolean preferPublic = getOrDefault(PREFER_PUBLIC_IP, false);
        int discoveryDelay = getOrDefault(DISCOVERY_DELAY, 10_000);
        String checkInterval = getOrDefault(CHECK_INTERVAL, "30s");
        this.applicationScope = getOrDefault(APPLICATION_SCOPE, "hazelcast");
        List<String> tags = Arrays.stream(getOrDefault(TAGS, "hazelcast").split(","))
                .collect(Collectors.toList());
        String healthCheckPath = getOrDefault(HEALTHCHECK_URL, "/health");
        Integer springBootPort = getOrDefault(SPRING_BOOT_PORT, 8080);
        String springBootProtocol = getOrDefault(SPRING_BOOT_PROTOCOL, "http");
        Address address = (preferPublic) ? discoveryNode.getPublicAddress() : discoveryNode.getPrivateAddress();
        String hostname = address.getHost();
        int port = address.getPort();

        String healthCheckUrl = springBootProtocol + "://" + hostname + ":" + springBootPort + healthCheckPath;

        String instanceId = getOrDefault(INSTANCE_ID, hostname + "-" + port + "-hazelcast");

        getLogger().info("Aplication Scope: " + this.applicationScope);
        getLogger().info("Aplication Tags: "+ tags);
        getLogger().info("Prefer public IP: " + preferPublic);
        getLogger().info("Discovery delay: " + discoveryDelay);
        getLogger().info("Check interval: " + checkInterval);
        getLogger().info("Health check URL: " + healthCheckUrl);
        getLogger().info("Instance ID: " + instanceId);

        NewService service = new NewService();
        service.setName(this.applicationScope);
        service.setAddress(hostname);
        service.setPort(port);
        service.setTags(tags);
        service.setId(instanceId);
        NewService.Check check = new NewService.Check();
        check.setInterval(checkInterval);
        check.setHttp(healthCheckUrl);
        service.setCheck(check);

        InetUtilsProperties inetUtilsProps = new InetUtilsProperties();
        inetUtilsProps.setDefaultHostname(hostname);
        inetUtilsProps.setDefaultIpAddress(hostname);
        inetUtilsProps.setTimeoutSeconds(discoveryDelay);
        inetUtilsProps.setUseOnlySiteLocalInterfaces(false);
        ConsulDiscoveryProperties consulDiscoveryProperties = new ConsulDiscoveryProperties(new InetUtils(inetUtilsProps));
        return new ConsulRegistration(service, consulDiscoveryProperties);
    }
}
