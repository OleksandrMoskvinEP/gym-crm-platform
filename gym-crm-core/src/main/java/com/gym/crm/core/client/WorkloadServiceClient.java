package com.gym.crm.core.client;

import com.gym.crm.core.client.dto.WorkloadRequest;
import com.gym.crm.core.client.impl.WorkloadServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "trainers-workload-service",
        path = "/api/v1/trainers-workload",
        fallback = WorkloadServiceFallback.class
)
public interface WorkloadServiceClient {
    @PostMapping
    ResponseEntity<Void> addWorkloadEvent(@RequestBody WorkloadRequest workloadRequest);
}
