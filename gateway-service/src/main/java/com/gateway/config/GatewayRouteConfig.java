package com.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfig {
    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("core-service", r -> r.path("/api/core/v1/**")
                        .uri("lb://gym-crm-core"))
                .route("workload-service", r -> r.path("/api/v1/trainers-workload/**")
                        .uri("lb://trainers-workload-service"))
                .build();
    }
}
