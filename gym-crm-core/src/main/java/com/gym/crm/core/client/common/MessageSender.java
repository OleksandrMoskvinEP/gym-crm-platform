package com.gym.crm.core.client.common;

import com.gym.crm.core.client.dto.WorkloadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageSender {
    private static final String DESTINATION_QUEUE = "core.to.workload.queue";

    private final JmsTemplate jmsTemplate;

    public void notifyWorkloadService(WorkloadRequest request) {
        jmsTemplate.convertAndSend(DESTINATION_QUEUE, request, message -> {
            message.setJMSCorrelationID(message.getJMSMessageID());

            return message;
        });
    }
}
