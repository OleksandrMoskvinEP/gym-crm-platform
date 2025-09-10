package com.gym.crm.workload.model.dto;

import lombok.Builder;

@Builder
public record IncreaseWorkloadParams(
        String username,
        String firstName,
        String lastName,
        boolean active,
        int workYear,
        int monthOfYear,
        long hoursDelta
) {
}
