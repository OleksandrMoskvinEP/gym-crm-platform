package com.gym.crm.integration.config;

import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static java.lang.String.format;

@CucumberContextConfiguration
@SpringBootTest
@Testcontainers
public class CucumberSpringConfiguration {
    private static final String ACTIVEMQ_IMAGE_NAME = "apache/activemq-classic:latest";
    private static final String MONGODB_IMAGE_NAME = "mongo:6.0";
    private static final String WORKLOAD_IMAGE_NAME = "gym-crm/workload:latest";

    private static final Network network = Network.newNetwork();

    private static final GenericContainer<?> mongoDBContainer = new GenericContainer<>(DockerImageName.parse(MONGODB_IMAGE_NAME))
            .withExposedPorts(27017)
            .withNetwork(network)
            .withNetworkAliases("mongo")
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "admin")
            .waitingFor(Wait.forListeningPort());

    private static final GenericContainer<?> activeMqContainer = new GenericContainer<>(DockerImageName.parse(ACTIVEMQ_IMAGE_NAME))
            .withExposedPorts(61616, 8161)
            .withNetwork(network)
            .withNetworkAliases("activemq")
            .waitingFor(Wait.forListeningPort());

    private static final GenericContainer<?> workloadContainer = new GenericContainer<>(DockerImageName.parse(WORKLOAD_IMAGE_NAME))
            .withExposedPorts(8082)
            .withNetwork(network)
            .dependsOn(mongoDBContainer, activeMqContainer)
            .withEnv("SPRING_DATA_MONGODB_URI", "mongodb://admin:admin@mongo:27017/gymcrm?authSource=admin")
            .withEnv("SPRING_ACTIVEMQ_BROKER_URL", "tcp://activemq:61616")
            .withEnv("SPRING_ACTIVEMQ_USER", "admin")
            .withEnv("SPRING_ACTIVEMQ_PASSWORD", "admin")
            .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200)
                    .withStartupTimeout(Duration.ofMinutes(2)));

    static {
        mongoDBContainer.start();
        activeMqContainer.start();
        workloadContainer.start();

        RestAssured.baseURI = "http://" + workloadContainer.getHost() + ":" + workloadContainer.getMappedPort(8082);
        System.out.println(">>> RestAssured baseURI = " + RestAssured.baseURI);
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () ->
                format("mongodb://admin:admin@%s:%d/gymcrm?authSource=admin",
                        mongoDBContainer.getHost(),
                        mongoDBContainer.getMappedPort(27017)));

        registry.add("spring.activemq.brokerUrl", () ->
                format("tcp://%s:%d",
                        activeMqContainer.getHost(),
                        activeMqContainer.getMappedPort(61616)));
        registry.add("spring.activemq.user", () -> "admin");
        registry.add("spring.activemq.password", () -> "admin");
        registry.add("jms.queue.trainer-workload", () -> "core.to.workload.queue");
    }
}