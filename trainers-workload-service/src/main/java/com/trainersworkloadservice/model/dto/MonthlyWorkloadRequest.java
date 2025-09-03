package com.trainersworkloadservice.model.dto;

import jakarta.validation.constraints.NotBlank;

public record MonthlyWorkloadRequest(
        @NotBlank String username,
        @NotBlank Short month,
        @NotBlank Short year
) {
}
