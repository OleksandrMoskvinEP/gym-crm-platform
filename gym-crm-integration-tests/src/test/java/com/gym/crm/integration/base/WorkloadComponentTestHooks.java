package com.gym.crm.integration.base;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

public class WorkloadComponentTestHooks {
    public static final String MONGO_URI = "mongodb://admin:admin@mongo:27017/gymcrm?authSource=admin";
    public static final String BROKER_URL = "tcp://activemq:61616";
    private static final String ACTIVEMQ_IMAGE_NAME = "rmohr/activemq:5.15.9";
    private static final String MONGODB_IMAGE_NAME = "mongo:6.0";
    public static final String WORKLOAD_IMAGE_NAME = "gym-crm/workload:latest";

    private static final Network network = Network.newNetwork();
    private static final GenericContainer<?> mongoDBContainer = getMongoDBContainer();
    private static final GenericContainer<?> activeMqContainer = getBrokerContainer();

    private static GenericContainer<?> workloadContainer;
    private static boolean started = false;

    @Before("@workload")
    public void start() {
        if (started) {
            return;
        }
        mongoDBContainer.start();
        activeMqContainer.start();

        workloadContainer = getContainer(MONGO_URI, BROKER_URL);
        workloadContainer.start();
        RestAssured.baseURI = getBaseURI();

        started = true;
    }

    @After("@workload")
    public void stop() {
        if (!started) {
            return;
        }
        workloadContainer.stop();
        activeMqContainer.stop();
        mongoDBContainer.stop();
        network.close();
        started = false;

    }

    private static @NotNull String getBaseURI() {
        return "http://" + workloadContainer.getHost() + ":" + workloadContainer.getMappedPort(8082);
    }

    private static GenericContainer<?> getContainer(String mongoUri, String brokerUrl) {
        return new GenericContainer<>(DockerImageName.parse(WORKLOAD_IMAGE_NAME))
                .withNetwork(network)
                .withExposedPorts(8082)
                .dependsOn(mongoDBContainer, activeMqContainer)
                .withEnv("SPRING_DATA_MONGODB_URI", mongoUri)
                .withEnv("SPRING_ACTIVEMQ_BROKER_URL", brokerUrl)
                .withEnv("SPRING_ACTIVEMQ_USER", "admin")
                .withEnv("SPRING_ACTIVEMQ_PASSWORD", "admin")
                .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200)
                        .withStartupTimeout(Duration.ofMinutes(2)));
    }

    private static GenericContainer<?> getMongoDBContainer() {
        return new GenericContainer<>(DockerImageName.parse(MONGODB_IMAGE_NAME))
                .withExposedPorts(27017)
                .withNetwork(network)
                .withNetworkAliases("mongo")
                .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin")
                .withEnv("MONGO_INITDB_ROOT_PASSWORD", "admin")
                .waitingFor(Wait.forListeningPort());
    }

    private static GenericContainer<?> getBrokerContainer() {
        return new GenericContainer<>(
                DockerImageName.parse(ACTIVEMQ_IMAGE_NAME))
                .withNetwork(network)
                .withNetworkAliases("activemq")
                .withExposedPorts(61616, 8161);
    }
}
