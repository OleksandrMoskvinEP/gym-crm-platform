package com.gym.crm.core.integration.workload.common;

import com.gym.crm.core.domain.dto.training.TrainingSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class PendingTrainingStore {
    private final Map<String, TrainingSaveRequest> correlationIdToTraining = new ConcurrentHashMap<>();

    public void put(String correlationId, TrainingSaveRequest request) {
        correlationIdToTraining.put(correlationId, request);
    }

    public Optional<TrainingSaveRequest> remove(String correlationId) {
        return Optional.ofNullable(correlationIdToTraining.remove(correlationId));
    }
}
