package com.trainersworkloadservice.model.dto;

public record DecreaseWorkloadParams(
        String username,
        int workYear,
        int monthOfYear,
        long hoursToRemove
) {
}
