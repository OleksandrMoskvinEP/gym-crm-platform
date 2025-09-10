package com.gym.crm.workload.exception;

public record ErrorResponse(
        String code,
        String message
) {
}
