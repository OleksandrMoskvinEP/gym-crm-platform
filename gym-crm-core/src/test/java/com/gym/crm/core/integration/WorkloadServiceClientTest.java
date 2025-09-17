package com.gym.crm.core.integration;

import com.gym.crm.core.integration.workload.WorkloadServiceClient;
import com.gym.crm.core.integration.workload.common.MessageSender;
import com.gym.crm.core.integration.workload.dto.WorkloadEventRequest;
import com.gym.crm.core.domain.dto.trainer.TrainerDto;
import com.gym.crm.core.domain.model.Trainer;
import com.gym.crm.core.domain.model.Training;
import com.gym.crm.core.domain.model.User;
import com.gym.crm.core.rest.TrainingCreateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceClientTest {
    @Mock
    private MessageSender messageSender;
    @InjectMocks
    private WorkloadServiceClient workloadServiceClient;

    @Test
    void shouldCallWorkloadService_Add_whenResponseIsOk() {
        TrainingCreateRequest request = getTrainingCreateRequest();
        TrainerDto trainer = getTrainerDto();

        doNothing().when(messageSender).notifyWorkloadService(any(WorkloadEventRequest.class));

        assertDoesNotThrow(() -> workloadServiceClient.callWorkloadServiceAdd(request, trainer));
        verify(messageSender).notifyWorkloadService(any(WorkloadEventRequest.class));
    }

    @Test
    void shouldCallWorkloadService_Delete() {
        Training training = getTraining();

        doNothing().when(messageSender).notifyWorkloadService(any(WorkloadEventRequest.class));

        assertDoesNotThrow(() -> workloadServiceClient.callWorkloadServiceDelete(training));
        verify(messageSender).notifyWorkloadService(any(WorkloadEventRequest.class));
    }

    private static TrainingCreateRequest getTrainingCreateRequest() {
        return new TrainingCreateRequest(
                "Yoga Session",
                LocalDate.now(),
                60,
                "trainee.username",
                "trainer.username"
        );
    }

    private static TrainerDto getTrainerDto() {
        TrainerDto trainer = new TrainerDto();
        trainer.setUsername("trainer.username");
        trainer.setFirstName("First");
        trainer.setLastName("Last");
        trainer.setActive(true);
        return trainer;
    }

    private static Training getTraining() {
        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setUsername("trainer.username");
        trainerDto.setFirstName("First");
        trainerDto.setLastName("Last");
        trainerDto.setActive(true);

        User user = User.builder()
                .username(trainerDto.getUsername())
                .firstName(trainerDto.getFirstName())
                .lastName(trainerDto.getLastName())
                .isActive(trainerDto.isActive())
                .build();


        Trainer trainer = Trainer.builder()
                .user(user)
                .build();

        return Training.builder()
                .trainer(trainer)
                .trainingDate(LocalDate.now())
                .trainingDuration(new BigDecimal(45))
                .trainingName("Yoga Session")
                .build();
    }
}