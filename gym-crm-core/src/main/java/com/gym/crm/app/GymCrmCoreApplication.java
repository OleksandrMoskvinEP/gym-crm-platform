package com.gym.crm.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GymCrmCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(GymCrmCoreApplication.class, args);
    }
}
