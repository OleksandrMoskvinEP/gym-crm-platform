package com.gym.crm.workload.service.common;

import com.gym.crm.workload.model.dto.WorkloadEventRequest;
import com.gym.crm.workload.model.dto.WorkloadEventResponse;
import com.gym.crm.workload.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageHandler {
    private final WorkloadService workloadService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = "core.to.workload.queue", containerFactory = "jmsListenerContainerFactory")
    public void receiveWorkloadEvent(@Payload WorkloadEventRequest request,
                                     @Header(name = "JMSMessageID", required = false) String correlationId) {
        try {
            log.info("Received Workload Event with correlationId={}: {}", correlationId, request);

            workloadService.calculateAndStoreWorkload(request);

            WorkloadEventResponse response = new WorkloadEventResponse(correlationId, "SUCCESS");
            jmsTemplate.convertAndSend("workload.to.core.queue", response);

            log.info("Workload Event with correlationId={} handled successfully", correlationId);
        } catch (Exception e) {
            log.error("Failed to process workload event: {} with correlationId={}", request, correlationId);
        }
    }
}
