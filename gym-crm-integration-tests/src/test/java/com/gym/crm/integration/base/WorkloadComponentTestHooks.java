package com.gym.crm.integration.base;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

public class WorkloadComponentTestHooks {

    private static final Network network = Network.newNetwork();

    private static final GenericContainer<?> mongoDBContainer = new GenericContainer<>(DockerImageName.parse("mongo:6.0"))
            .withExposedPorts(27017)
            .withNetwork(network)
            .withNetworkAliases("mongo")
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "admin")
            .waitingFor(Wait.forListeningPort());

    private static final GenericContainer<?> activeMqContainer = new GenericContainer<>(
            DockerImageName.parse("rmohr/activemq:5.15.9"))
            .withNetwork(network)
            .withNetworkAliases("activemq")
            .withExposedPorts(61616, 8161);

    private static GenericContainer<?> workloadContainer;

    private static boolean started = false;

    @Before("@workload")
    public void start() {
        if (!started) {
            mongoDBContainer.start();
            activeMqContainer.start();

            String mongoUri = "mongodb://admin:admin@mongo:27017/gymcrm?authSource=admin";
            String brokerUrl = "tcp://activemq:61616";

            workloadContainer = new GenericContainer<>(DockerImageName.parse("gym-crm/workload:latest"))
                    .withNetwork(network)
                    .withExposedPorts(8082)
                    .dependsOn(mongoDBContainer, activeMqContainer)
                    .withEnv("SPRING_DATA_MONGODB_URI", mongoUri)
                    .withEnv("SPRING_ACTIVEMQ_BROKER_URL", brokerUrl)
                    .withEnv("SPRING_ACTIVEMQ_USER", "admin")
                    .withEnv("SPRING_ACTIVEMQ_PASSWORD", "admin")
                    .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200)
                            .withStartupTimeout(Duration.ofMinutes(2)));

            workloadContainer.start();

            RestAssured.baseURI = "http://" + workloadContainer.getHost() + ":" + workloadContainer.getMappedPort(8082);

            started = true;
        }
    }

    @After("@workload")
    public void stop() {
        if (started) {
            workloadContainer.stop();
            activeMqContainer.stop();
            mongoDBContainer.stop();
            network.close();
            started = false;
        }
    }
}
