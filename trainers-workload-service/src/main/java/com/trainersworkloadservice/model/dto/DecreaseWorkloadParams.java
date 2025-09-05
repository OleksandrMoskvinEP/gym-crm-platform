package com.trainersworkloadservice.model.dto;

import lombok.Builder;

@Builder
public record DecreaseWorkloadParams(
        String username,
        int workYear,
        int monthOfYear,
        long hoursToRemove
) {
}
