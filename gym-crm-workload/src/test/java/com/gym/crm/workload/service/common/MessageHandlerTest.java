package com.gym.crm.workload.service.common;

import com.gym.crm.workload.model.dto.WorkloadEventRequest;
import com.gym.crm.workload.model.dto.WorkloadEventResponse;
import com.gym.crm.workload.service.WorkloadService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = MessageHandlerTest.class)
@ActiveProfiles("test")
class MessageHandlerTest {
    @Mock
    private WorkloadService workloadService;
    @Mock
    private JmsTemplate jmsTemplate;
    @InjectMocks
    private MessageHandler messageHandler;

    @Test
    void shouldHandleEventDirectly() {
        ArgumentCaptor<WorkloadEventResponse> captor = ArgumentCaptor.forClass(WorkloadEventResponse.class);
        WorkloadEventRequest request = getEventRequest();
        messageHandler.receiveWorkloadEvent(request, UUID.randomUUID().toString());

        verify(jmsTemplate).convertAndSend(eq("workload.to.core.queue"), captor.capture());
        WorkloadEventResponse response = captor.getValue();
        assertEquals("SUCCESS", response.status());

        verify(workloadService).calculateAndStoreWorkload(any());

    }

    private static WorkloadEventRequest getEventRequest() {
        return new WorkloadEventRequest(
                "arnold", "Arnold", "Schwarzenegger",
                true, LocalDate.of(2025, 9, 17), 90, "ADD"
        );
    }
}