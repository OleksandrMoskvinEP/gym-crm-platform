package com.trainersworkloadservice.model.dto;

import jakarta.validation.constraints.NotBlank;

public record MonthlyWorkloadRequest(
        @NotBlank String username,
        @NotBlank int year,
        @NotBlank int month
) {
}
