package com.gym.crm.integration.crosservice;

import com.gym.crm.integration.IntegrationTestApplication;
import io.cucumber.spring.CucumberContextConfiguration;
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
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CrosserviceTestConfiguration {
    private static final String BROKER_IMAGE_NAME = "apache/activemq-classic:latest";
    private static final String MONGODB_IMAGE_NAME = "mongo:6.0";
    private static final String WORKLOAD_IMAGE_NAME = "gym-crm/workload:latest";
    public static final String POSTGRESQL_IMAGE_NAME = "postgres:latest";
    public static final String CORE_IMAGE_NAME = "gym-crm/core:latest";
    private static final String DISCOVERY_IMAGE_NAME = "gym-crm/discovery:latest";
    private static final String GATEWAY_IMAGE_NAME = "gym-crm/gateway:latest";

    private static final Network NETWORK = Network.newNetwork();

    private static final GenericContainer<?> MONGO_DB_CONTAINER = getMongodbContainer();
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = getPostgresContainer();
    private static final GenericContainer<?> ACTIVEMQ_CONTAINER = getActivemqContainer();
    private static final GenericContainer<?> DISCOVERY_CONTAINER = getDiscoveryContainer();
    private static final GenericContainer<?> GATEWAY_CONTAINER = getGatewayContainer();
    private static final GenericContainer<?> CORE_CONTAINER = getCoreContainer();
    private static final GenericContainer<?> WORKLOAD_CONTAINER = getWorkloadContainer();

    static {
        POSTGRES_CONTAINER.start();
        MONGO_DB_CONTAINER.start();
        ACTIVEMQ_CONTAINER.start();
        DISCOVERY_CONTAINER.start();
        GATEWAY_CONTAINER.start();
        CORE_CONTAINER.start();
        WORKLOAD_CONTAINER.start();

        WORKLOAD_CONTAINER.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger("WORKLOAD")));
        CORE_CONTAINER.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger("CORE")));
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);

        registry.add("spring.data.mongodb.uri", () ->
                format("mongodb://test:test@%s:%d/gymcrm?authSource=admin",
                        MONGO_DB_CONTAINER.getHost(),
                        MONGO_DB_CONTAINER.getMappedPort(27017)));

        registry.add("spring.activemq.brokerUrl", () ->
                format("tcp://%s:%d",
                        ACTIVEMQ_CONTAINER.getHost(),
                        ACTIVEMQ_CONTAINER.getMappedPort(61616)));
        registry.add("spring.activemq.user", () -> "test");
        registry.add("spring.activemq.password", () -> "test");

        registry.add("jms.queue.trainer-workload", () -> "core.to.workload.queue");
        registry.add("jms.queue.workload-core", () -> "workload.to.core.queue");
    }

    private static GenericContainer<?> getMongodbContainer() {
        return new GenericContainer<>(DockerImageName.parse(MONGODB_IMAGE_NAME))
                .withExposedPorts(27017)
                .withNetwork(NETWORK)
                .withNetworkAliases("mongo")
                .withEnv("MONGO_INITDB_ROOT_USERNAME", "test")
                .withEnv("MONGO_INITDB_ROOT_PASSWORD", "test")
                .waitingFor(Wait.forListeningPort());
    }

    private static GenericContainer<?> getActivemqContainer() {
        return new GenericContainer<>(DockerImageName.parse(BROKER_IMAGE_NAME))
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
                .withEnv("SPRING_DATA_MONGODB_URI", "mongodb://test:test@mongo:27017/gymcrm?authSource=admin")
                .withEnv("SPRING_ACTIVEMQ_BROKER_URL", "tcp://activemq:61616")
                .withEnv("SPRING_ACTIVEMQ_USER", "test")
                .withEnv("SPRING_ACTIVEMQ_PASSWORD", "test")
                .withEnv("EUREKA_CLIENT_SERVICEURL_DEFAULTZONE", "http://discovery:8761/eureka")
                .withEnv("JMS_QUEUE_TRAINER_WORKLOAD", "core.to.workload.queue")
                .withEnv("JMS_QUEUE_WORKLOAD_CORE", "workload.to.core")
                .withEnv("EUREKA_CLIENT_SERVICEURL_DEFAULTZONE", "http://discovery:8761/eureka")
                .withEnv("JMS_QUEUE_TRAINER_WORKLOAD", "core.to.workload.queue")
                .withEnv("JMS_QUEUE_WORKLOAD_CORE", "workload.to.core")
                .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200)
                        .withStartupTimeout(Duration.ofMinutes(2)));
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

    private static PostgreSQLContainer<?> getPostgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse(POSTGRESQL_IMAGE_NAME))
                .withDatabaseName("gym-crm")
                .withUsername("test")
                .withPassword("test")
                .withNetwork(NETWORK)
                .withNetworkAliases("postgres")
                .waitingFor(Wait.forListeningPort());
    }

    private static GenericContainer<?> getDiscoveryContainer() {
        return new GenericContainer<>(DockerImageName.parse(DISCOVERY_IMAGE_NAME))
                .withExposedPorts(8761)
                .withNetwork(NETWORK)
                .withNetworkAliases("discovery")
                .withEnv("EUREKA_CLIENT_REGISTER-WITH-EUREKA", "false")
                .withEnv("EUREKA_CLIENT_FETCH-REGISTRY", "false")
                .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)));
    }

    private static GenericContainer<?> getGatewayContainer() {
        return new GenericContainer<>(DockerImageName.parse(GATEWAY_IMAGE_NAME))
                .withExposedPorts(8080)
                .withNetwork(NETWORK)
                .withNetworkAliases("gateway")
                .withEnv("EUREKA_CLIENT_SERVICEURL_DEFAULTZONE", "http://discovery:8761/eureka")
                .withEnv("EUREKA_CLIENT_FETCH_REGISTRY", "true")
                .withEnv("EUREKA_CLIENT_REGISTER_WITH_EUREKA", "false")
                .withEnv("SPRING_CLOUD_DISCOVERY_ENABLED", "true")
                .withEnv("EUREKA_INSTANCE_PREFER_IP_ADDRESS", "true")
                .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)));
    }
}
