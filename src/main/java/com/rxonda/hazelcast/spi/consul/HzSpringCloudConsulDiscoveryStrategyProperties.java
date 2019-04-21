/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.rxonda.hazelcast.spi.consul;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.SimplePropertyDefinition;

import static com.hazelcast.config.properties.PropertyTypeConverter.*;

public class HzSpringCloudConsulDiscoveryStrategyProperties {
    public static final PropertyDefinition APPLICATION_SCOPE =
            new SimplePropertyDefinition("consul.name", true, STRING);

    public static final PropertyDefinition TAGS =
            new SimplePropertyDefinition("consul.tags", true, STRING);

    public static final PropertyDefinition CHECK_INTERVAL =
            new SimplePropertyDefinition("consul.check-interval", true, STRING);

    public static final PropertyDefinition PREFER_PUBLIC_IP =
            new SimplePropertyDefinition("consul.prefer-public", true, BOOLEAN);

    public static final PropertyDefinition DISCOVERY_DELAY =
            new SimplePropertyDefinition("consul.discovery-delay", true, INTEGER);

    public static final PropertyDefinition HEALTHCHECK_URL =
            new SimplePropertyDefinition("consul.healthcheck-url", true, STRING);
}
