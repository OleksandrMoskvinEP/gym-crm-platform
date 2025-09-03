package com.trainersworkloadservice.model.dto;

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
