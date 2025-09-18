package com.gym.crm.core.integration.workload.common;

import com.gym.crm.core.integration.workload.dto.WorkloadEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageSender {
    private static final String DESTINATION_QUEUE = "core.to.workload.queue";

    private final JmsTemplate jmsTemplate;

    public String notifyWorkloadService(WorkloadEventRequest request) {
        String id = UUID.randomUUID().toString();
        log.info("Sending message with request: {}  to workload service with ID: {}", request, id);

        jmsTemplate.convertAndSend(DESTINATION_QUEUE, request, message -> {
            message.setJMSCorrelationID(id);

            return message;
        });

        return id;
    }
}
