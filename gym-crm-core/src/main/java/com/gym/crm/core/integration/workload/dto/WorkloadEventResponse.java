package com.gym.crm.core.integration.workload.dto;

import jakarta.validation.constraints.NotBlank;

public record WorkloadEventResponse(
        @NotBlank String correlationId,
        @NotBlank String status
) {
}
