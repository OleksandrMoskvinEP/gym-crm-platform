package com.gym.crm.integration.config;

import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
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

    private static final Network NETWORK = Network.newNetwork();

    private static final GenericContainer<?> MONGO_DB_CONTAINER = getMongodbContainer();
    private static final GenericContainer<?> ACTIVEMQ_CONTAINER = getActivemqContainer();
    private static final GenericContainer<?> WORKLOAD_CONTAINER = getWorkloadContainer();

    static {
        MONGO_DB_CONTAINER.start();
        ACTIVEMQ_CONTAINER.start();
        WORKLOAD_CONTAINER.start();

        RestAssured.baseURI = "http://" + WORKLOAD_CONTAINER.getHost() + ":" + WORKLOAD_CONTAINER.getMappedPort(8082);
        WORKLOAD_CONTAINER.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger("WORKLOAD")));
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () ->
                format("mongodb://admin:admin@%s:%d/gymcrm?authSource=admin",
                        MONGO_DB_CONTAINER.getHost(),
                        MONGO_DB_CONTAINER.getMappedPort(27017)));

        registry.add("spring.activemq.brokerUrl", () ->
                format("tcp://%s:%d",
                        ACTIVEMQ_CONTAINER.getHost(),
                        ACTIVEMQ_CONTAINER.getMappedPort(61616)));
        registry.add("spring.activemq.user", () -> "admin");
        registry.add("spring.activemq.password", () -> "admin");
        registry.add("jms.queue.trainer-workload", () -> "core.to.workload.queue");
    }

    private static GenericContainer<?> getMongodbContainer() {
        return new GenericContainer<>(DockerImageName.parse(MONGODB_IMAGE_NAME))
                .withExposedPorts(27017)
                .withNetwork(NETWORK)
                .withNetworkAliases("mongo")
                .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin")
                .withEnv("MONGO_INITDB_ROOT_PASSWORD", "admin")
                .waitingFor(Wait.forListeningPort());
    }

    private static GenericContainer<?> getActivemqContainer() {
        return new GenericContainer<>(DockerImageName.parse(ACTIVEMQ_IMAGE_NAME))
                .withExposedPorts(61616, 8161)
                .withNetwork(NETWORK)
                .withNetworkAliases("activemq")
                .waitingFor(Wait.forListeningPort());
    }

    private static GenericContainer<?> getWorkloadContainer() {
        return new GenericContainer<>(DockerImageName.parse(WORKLOAD_IMAGE_NAME))
                .withExposedPorts(8082)
                .withNetwork(NETWORK)
                .dependsOn(MONGO_DB_CONTAINER, ACTIVEMQ_CONTAINER)
                .withEnv("SPRING_DATA_MONGODB_URI", "mongodb://admin:admin@mongo:27017/gymcrm?authSource=admin")
                .withEnv("SPRING_ACTIVEMQ_BROKER_URL", "tcp://activemq:61616")
                .withEnv("SPRING_ACTIVEMQ_USER", "admin")
                .withEnv("SPRING_ACTIVEMQ_PASSWORD", "admin")
                .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200)
                        .withStartupTimeout(Duration.ofMinutes(2)));
    }
}