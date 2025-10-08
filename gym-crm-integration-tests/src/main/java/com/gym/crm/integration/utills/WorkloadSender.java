package com.gym.crm.integration.utills;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorkloadSender {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${jms.queue.trainer-workload}")
    private String queueName;

    public WorkloadSender(JmsTemplate jmsTemplate, ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
    }

    public void send(Map<String, Object> payload, String typeId) {
        try {
            String json = objectMapper.writeValueAsString(payload);

            jmsTemplate.convertAndSend(queueName, json, message -> {
                if (typeId != null && !typeId.isBlank()) {
                    message.setStringProperty("_type", typeId);
                }

                return message;
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }
    }
}