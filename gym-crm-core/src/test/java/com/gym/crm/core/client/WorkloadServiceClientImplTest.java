package com.gym.crm.core.client;

import com.gym.crm.core.client.dto.WorkloadRequest;
import com.gym.crm.core.client.impl.WorkloadServiceClientImpl;
import com.gym.crm.core.domain.dto.trainer.TrainerDto;
import com.gym.crm.core.exception.CoreServiceException;
import com.gym.crm.core.rest.TrainingCreateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceClientImplTest {
    @Mock
    private WorkloadServiceClient client;
    @InjectMocks
    private WorkloadServiceClientImpl workloadServiceClient;

    @Test
    void shouldCallWorkloadService_whenResponseIsOk() {
        TrainingCreateRequest request = getTrainingCreateRequest();
        TrainerDto trainer = getTrainerDto();

        when(client.addWorkloadEvent(any()))
                .thenReturn(ResponseEntity.ok().build());

        assertDoesNotThrow(() -> workloadServiceClient.callWorkloadService(request, trainer));

        verify(client).addWorkloadEvent(any(WorkloadRequest.class));
    }


    @Test
    void shouldThrowException_whenResponseIsNotOk() {
        TrainingCreateRequest request = getTrainingCreateRequest();
        TrainerDto trainer = getTrainerDto();

        when(client.addWorkloadEvent(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        assertThrows(CoreServiceException.class,
                () -> workloadServiceClient.callWorkloadService(request, trainer));

        verify(client).addWorkloadEvent(any(WorkloadRequest.class));
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
}