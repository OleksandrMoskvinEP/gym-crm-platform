package com.gym.crm.core.integration.workload.common;

import com.gym.crm.core.integration.workload.dto.WorkloadEventRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageSenderTest {
    @Mock
    private JmsTemplate jmsTemplate;
    @InjectMocks
    private MessageSender messageSender;

    @Test
    void shouldSendMessageAndReturnCorrelationId() {
        WorkloadEventRequest request = getEventRequest();

        String correlationId = messageSender.notifyWorkloadService(request);

        assertNotNull(correlationId);
        assertFalse(correlationId.isBlank());

        verify(jmsTemplate).convertAndSend(
                eq("core.to.workload.queue"),
                eq(request),
                any()
        );
    }

    @Test
    void shouldGenerateDifferentCorrelationIdsForDifferentCalls() {
        WorkloadEventRequest request = getEventRequest();

        String id1 = messageSender.notifyWorkloadService(request);
        String id2 = messageSender.notifyWorkloadService(request);

        assertNotEquals(id1, id2);
    }

    private static WorkloadEventRequest getEventRequest() {
        return new WorkloadEventRequest(
                "first_last",
                "First",
                "Last",
                true,
                LocalDate.of(2025, 9, 18), 90,
                "ADD"
        );
    }
}