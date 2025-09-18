package com.gym.crm.core.integration.workload.common;

import com.gym.crm.core.domain.dto.training.TrainingDto;
import com.gym.crm.core.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.core.integration.workload.dto.WorkloadEventResponse;
import com.gym.crm.core.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {
    private final PendingTrainingStore pendingTrainingStore;
    private final TrainingService trainingService;

    @JmsListener(destination = "workload.to.core.queue", containerFactory = "jmsListenerContainerFactory")
    public void handleWorkloadAck(@Payload WorkloadEventResponse response) {
        log.info("Received workload ack: {}", response);

        if (response == null || response.correlationId() == null) {
            log.warn("Received null or invalid workload response");

            return;
        }

        Optional<TrainingSaveRequest> requestOpt = pendingTrainingStore.remove(response.correlationId());
        if (requestOpt.isEmpty()) {
            log.warn("No pending training found for correlationId - {}", response.correlationId());

            return;
        }

        if (!"SUCCESS".equalsIgnoreCase(response.status())) {
            log.warn("Workload responded with non-success for correlationId - {}", response.correlationId());

            return;
        }

        TrainingSaveRequest saveRequest = requestOpt.get();
        TrainingDto saved = trainingService.addTraining(saveRequest);
        log.info("Training persisted after workload ack, training name - {}, date - {}", saved.getTrainingName(), saved.getTrainingDate());
    }
}
