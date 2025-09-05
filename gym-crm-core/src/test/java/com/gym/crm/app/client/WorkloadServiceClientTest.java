package com.gym.crm.app.client;

import com.gym.crm.app.domain.dto.trainer.TrainerDto;
import com.gym.crm.app.exception.CoreServiceException;
import com.gym.crm.app.rest.TrainingCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceClientTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private WorkloadServiceClient client;

    @BeforeEach
    void setUp() throws Exception {
        Field urlField = WorkloadServiceClient.class.getDeclaredField("workloadUrl");
        urlField.setAccessible(true);
        urlField.set(client, "http://dummy");
    }

    @Test
    void shouldCallWorkloadService_whenResponseIsOk() {
        TrainingCreateRequest request = getTrainingCreateRequest();
        TrainerDto trainer = getTrainerDto();

        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        assertDoesNotThrow(() -> client.callWorkloadService(request, trainer));
    }

    @Test
    void shouldThrowException_whenResponseIsNotOk() {
        TrainingCreateRequest request = getTrainingCreateRequest();
        TrainerDto trainer = getTrainerDto();

        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        assertThrows(CoreServiceException.class, () -> client.callWorkloadService(request, trainer));
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