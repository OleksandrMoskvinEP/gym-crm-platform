package com.gym.crm.workload.model.dto;

import jakarta.validation.constraints.NotBlank;

public record MonthlyWorkloadRequest(
        @NotBlank String username,
        @NotBlank int year,
        @NotBlank int month
) {
}
