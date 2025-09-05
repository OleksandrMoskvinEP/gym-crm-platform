package com.gym.crm.app.client;

import com.gym.crm.app.client.dto.WorkloadRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerDto;
import com.gym.crm.app.exception.CoreServiceException;
import com.gym.crm.app.rest.TrainingCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WorkloadServiceClient {
    private final RestTemplate restTemplate;

    @Value("${workload.service-url}")
    private String workloadUrl;

    public void callWorkloadService(@Valid TrainingCreateRequest request, TrainerDto trainer) {
        WorkloadRequest workloadEventRequest = new WorkloadRequest(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.isActive(),
                request.getTrainingDate(),
                request.getTrainingDuration(),
                request.getTrainingDuration() > 0 ? "ADD" : "DELETE"
        );

        ResponseEntity<Void> response = restTemplate.postForEntity(
                workloadUrl,
                workloadEventRequest,
                Void.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CoreServiceException("Workload service returned error: " + response.getStatusCode());
        }
    }
}
