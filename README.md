# Hazelcast Service Discovery for Spring Cloud applications

## This is a Spring Boot autoconfigurable implementation of Hazelcast Service Discovery.

## Usage
Generate the jar library with
```bash
$ ./gradlew clean jar
```
Then, add it to your projects libs.

Or, we can use [JitPack](https://jitpack.io)
Then, add it to your dependencies:
```groovy
compile 'com.github.rxonda:SpringCloudHazelcastDiscoverySpi:v1.0-alpha'
```

In the hazelcast Config bean, add it to JoinConfig and mark Tcp and Multicast to false:
```java
@Bean Config config(DiscoveryStrategyConfig discoveryStrategyConfig) {
	Config config = new Config();
	config.getNetworkConfig()
		.getJoinConfig()
			.setMulticastConfig(new MulticastConfig().setEnabled(false))
	                .setTcpIpConfig(new TcpIpConfig().setEnabled(false))
        	        .setAwsConfig(new AwsConfig().setEnabled(false))
			.getDiscoveryConfig()
				.addDiscoveryStrategyConfig(discoveryStrategyConfig);
	return config;
}
```

The discoveryStrategyConfig will be created by the autoconfigure spring boot feature.

## Configuration
* hazelcast.discovery.consul.group-name: consul group name
* hazelcast.discovery.consul.tags: consul tags
* hazelcast.discovery.consul.discovery-delay: consul discovery delay
* hazelcast.discovery.consul.check-interval: consul check interval
* hazelcast.discovery.consul.prefer-public: to registry the public ip on consul registry
* hazelcast.discovery.consul.instance-id: the instance id
* hazelcast.discovery.consul.healthcheck-url: path to application's health check endpoint

## Considerations
For http health check works correctly, we have to provide the following configurations of the spring boot application:
* server.port (defaults to 8080)
* server.protocol (defaults to http)
This is necessary because Hazelcast instance don't expose a http/https health check endpoints.

To activate the Service Discovery subsystem on Hazelcast, we have to set the property `hazelcast.discovery.enabled` to true, either with jvm setProperty or in the Config setProperty.
ex.
```java
Config config = new Config();
config.setProperty("hazelcast.discovery.enabled", "true");
```

