package com.gymcommon.workload;

import jakarta.validation.constraints.NotBlank;

public record MonthlyWorkloadRequest(
        @NotBlank String username,
        @NotBlank Integer month,
        @NotBlank Integer year
) {
}
