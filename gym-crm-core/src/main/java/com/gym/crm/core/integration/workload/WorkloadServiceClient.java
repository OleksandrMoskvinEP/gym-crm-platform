package com.gym.crm.core.integration.workload;

import com.gym.crm.core.integration.workload.common.MessageSender;
import com.gym.crm.core.integration.workload.dto.WorkloadEventRequest;
import com.gym.crm.core.domain.dto.trainer.TrainerDto;
import com.gym.crm.core.domain.model.Training;
import com.gym.crm.core.rest.TrainingCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadServiceClient {
    private final MessageSender messageSender;

    public String callWorkloadServiceAdd(@Valid TrainingCreateRequest request, TrainerDto trainer) {
        WorkloadEventRequest workloadEventRequest = new WorkloadEventRequest(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.isActive(),
                request.getTrainingDate(),
                request.getTrainingDuration(),
                request.getTrainingDuration() > 0 ? "ADD" : "DELETE"
        );

        return messageSender.notifyWorkloadService(workloadEventRequest);
    }

    public void callWorkloadServiceDelete(Training training) {
        WorkloadEventRequest workloadEventRequest = new WorkloadEventRequest(
                training.getTrainer().getUser().getUsername(),
                training.getTrainer().getUser().getFirstName(),
                training.getTrainer().getUser().getLastName(),
                training.getTrainer().getUser().getIsActive(),
                training.getTrainingDate(),
                training.getTrainingDuration().intValue(),
                "DELETE"
        );

        messageSender.notifyWorkloadService(workloadEventRequest);
    }
}
