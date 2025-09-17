package com.gym.crm.workload.model.dto;

import jakarta.validation.constraints.NotBlank;

public record WorkloadEventResponse(
        @NotBlank String correlationId,
        @NotBlank String status
) {
}
