package com.gym.crm.core.client.impl;

import com.gym.crm.core.client.WorkloadServiceClient;
import com.gym.crm.core.client.dto.WorkloadRequest;
import com.gym.crm.core.domain.dto.trainer.TrainerDto;
import com.gym.crm.core.domain.model.Training;
import com.gym.crm.core.exception.CoreServiceException;
import com.gym.crm.core.rest.TrainingCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadServiceClientImpl {
    private final WorkloadServiceClient workloadClient;

    public void callWorkloadServiceAdd(@Valid TrainingCreateRequest request, TrainerDto trainer) {
        WorkloadRequest workloadEventRequest = new WorkloadRequest(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.isActive(),
                request.getTrainingDate(),
                request.getTrainingDuration(),
                request.getTrainingDuration() > 0 ? "ADD" : "DELETE"
        );

        ResponseEntity<Void> response = workloadClient.addWorkloadEvent(workloadEventRequest);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CoreServiceException("Workload service returned error: " + response.getStatusCode());
        }
    }

    public void callWorkloadServiceDelete(Training training) {
        WorkloadRequest workloadEventRequest = new WorkloadRequest(
                training.getTrainer().getUser().getUsername(),
                training.getTrainer().getUser().getFirstName(),
                training.getTrainer().getUser().getLastName(),
                training.getTrainer().getUser().getIsActive(),
                training.getTrainingDate(),
                training.getTrainingDuration().intValue(),
                "DELETE"
        );

        ResponseEntity<Void> response = workloadClient.addWorkloadEvent(workloadEventRequest);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CoreServiceException("Workload service returned error: " + response.getStatusCode());
        }
    }
}
