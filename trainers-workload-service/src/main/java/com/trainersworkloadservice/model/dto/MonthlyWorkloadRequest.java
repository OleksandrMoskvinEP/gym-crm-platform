package com.trainersworkloadservice.model.dto;

import jakarta.validation.constraints.NotBlank;

public record MonthlyWorkloadRequest(
        @NotBlank String username,
        @NotBlank int month,
        @NotBlank int year
) {
}
