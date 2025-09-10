package com.gym.crm.core.client.impl;

import com.gym.crm.core.client.WorkloadServiceClient;
import com.gym.crm.core.client.dto.WorkloadRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WorkloadServiceFallback implements WorkloadServiceClient {
    @Override
    public ResponseEntity<Void> addWorkloadEvent(WorkloadRequest workloadRequest) {
        log.error("Fallback triggered! Workload service unavailable for {}", workloadRequest);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}

