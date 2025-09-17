package com.gym.crm.core.client.common;

import com.gym.crm.core.client.dto.WorkloadEventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageSender {
    private static final String DESTINATION_QUEUE = "core.to.workload.queue";

    private final JmsTemplate jmsTemplate;

    public void notifyWorkloadService(WorkloadEventRequest request) {
        String id = UUID.randomUUID().toString();

        jmsTemplate.convertAndSend(DESTINATION_QUEUE, request, message -> {
            message.setJMSCorrelationID(id);

            return message;
        });
    }
}
