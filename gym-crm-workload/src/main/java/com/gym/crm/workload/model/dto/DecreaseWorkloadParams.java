package com.gym.crm.workload.model.dto;

import lombok.Builder;

@Builder
public record DecreaseWorkloadParams(
        String username,
        int workYear,
        int monthOfYear,
        long hoursToRemove
) {
}
