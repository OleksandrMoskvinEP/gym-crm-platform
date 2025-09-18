package com.gym.crm.core.integration.workload.common;

import com.gym.crm.core.domain.dto.training.TrainingDto;
import com.gym.crm.core.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.core.integration.workload.dto.WorkloadEventResponse;
import com.gym.crm.core.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageListenerTest {
    @Mock
    private PendingTrainingStore pendingTrainingStore;
    @Mock
    private TrainingService trainingService;
    @InjectMocks
    private MessageListener messageListener;

    @Test
    void shouldPersistTraining_whenResponseIsSuccess() {
        String correlationId = "corr-123";
        TrainingSaveRequest saveRequest = new TrainingSaveRequest();
        WorkloadEventResponse response = new WorkloadEventResponse(correlationId, "SUCCESS");

        TrainingDto expectedDto = new TrainingDto();
        expectedDto.setTrainingName("Yoga");
        expectedDto.setTrainingDate(LocalDate.now());

        when(pendingTrainingStore.remove(correlationId)).thenReturn(Optional.of(saveRequest));
        when(trainingService.addTraining(saveRequest)).thenReturn(expectedDto);

        messageListener.handleWorkloadAck(response);

        verify(trainingService).addTraining(saveRequest);
    }

    @Test
    void shouldNotPersist_whenResponseIsNull() {
        messageListener.handleWorkloadAck(null);

        verifyNoInteractions(trainingService);
        verifyNoInteractions(pendingTrainingStore);
    }

    @Test
    void shouldNotPersist_whenCorrelationIdIsNull() {
        WorkloadEventResponse response = new WorkloadEventResponse(null, "SUCCESS");

        messageListener.handleWorkloadAck(response);

        verifyNoInteractions(trainingService);
        verifyNoInteractions(pendingTrainingStore);
    }

    @Test
    void shouldNotPersist_whenNoPendingTrainingFound() {
        String correlationId = "corr-123";
        WorkloadEventResponse response = new WorkloadEventResponse(correlationId, "SUCCESS");

        when(pendingTrainingStore.remove(correlationId)).thenReturn(Optional.empty());

        messageListener.handleWorkloadAck(response);

        verifyNoInteractions(trainingService);
    }

    @Test
    void shouldNotPersist_whenStatusIsNotSuccess() {
        String correlationId = "corr-123";
        WorkloadEventResponse response = new WorkloadEventResponse(correlationId, "ERROR");

        when(pendingTrainingStore.remove(correlationId)).thenReturn(Optional.of(new TrainingSaveRequest()));

        messageListener.handleWorkloadAck(response);

        verifyNoInteractions(trainingService);
    }
}