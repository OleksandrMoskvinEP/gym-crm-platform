package com.trainersworkloadservice.model.dto;

public record MonthlyWorkloadResponse(
        String username,
        Short year,
        Short month,
        Long totalHours,
        Long totalMinutes
) {
}
