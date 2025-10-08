package com.gym.crm.integration.core;

import com.gym.crm.integration.IntegrationTestApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static java.lang.String.format;

@CucumberContextConfiguration
@SpringBootTest(classes = IntegrationTestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CoreTestConfiguration {
    private static final Network NETWORK = Network.newNetwork();

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = getPostgresContainer();
    private static final GenericContainer<?> ACTIVEMQ_CONTAINER = getBrokerContainer();
    private static final GenericContainer<?> CORE_CONTAINER = getCoreContainer();

    public static final String BROKER_IMAGE_NAME = "rmohr/activemq:latest";
    public static final String POSTGRESQL_IMAGE_NAME = "postgres:latest";
    public static final String CORE_IMAGE_NAME = "gym-crm/core:latest";

    static {
        POSTGRES_CONTAINER.start();
        ACTIVEMQ_CONTAINER.start();
        CORE_CONTAINER.start();

        RestAssured.baseURI = "http://" + CORE_CONTAINER.getHost() + ":" + CORE_CONTAINER.getMappedPort(8081);
        CORE_CONTAINER.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger("CORE")));
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.activemq.broker-url", () ->
                format("tcp://%s:%d",
                        ACTIVEMQ_CONTAINER.getHost(),
                        ACTIVEMQ_CONTAINER.getMappedPort(61616)));
        registry.add("spring.activemq.user", () -> "test");
        registry.add("spring.activemq.password", () -> "test");
        registry.add("jms.queue.core-workload", () -> "core.to.workload.queue");
        registry.add("jms.queue.workload-core", () -> "workload.to.core.queue");
    }

    private static GenericContainer<?> getCoreContainer() {
        return new GenericContainer<>(DockerImageName.parse(CORE_IMAGE_NAME))
                .withExposedPorts(8081)
                .withNetwork(NETWORK)
                .dependsOn(POSTGRES_CONTAINER, ACTIVEMQ_CONTAINER)
                .withEnv("SPRING_PROFILES_ACTIVE", "integration-tests")
                .withEnv("JMS_QUEUE_TRAINER_WORKLOAD", "core.to.workload.queue")
                .waitingFor(Wait.forHttp("/actuator/health")
                        .forStatusCode(200)
                        .withStartupTimeout(Duration.ofSeconds(30)));
    }

    private static GenericContainer<?> getBrokerContainer() {
        return new GenericContainer<>(DockerImageName.parse(BROKER_IMAGE_NAME))
                .withEnv("ACTIVEMQ_USER", "test")
                .withEnv("ACTIVEMQ_PASSWORD", "test")
                .withExposedPorts(61616, 8161)
                .withNetworkAliases("activemq")
                .withNetwork(NETWORK);
    }

    private static PostgreSQLContainer<?> getPostgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse(POSTGRESQL_IMAGE_NAME))
                .withDatabaseName("gym-crm")
                .withUsername("test")
                .withPassword("test")
                .withNetwork(NETWORK)
                .withNetworkAliases("postgres")
                .waitingFor(Wait.forListeningPort());
    }
}