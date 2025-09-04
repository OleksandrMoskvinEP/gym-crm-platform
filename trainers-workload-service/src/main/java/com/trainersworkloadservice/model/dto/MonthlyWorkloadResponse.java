package com.trainersworkloadservice.model.dto;

public record MonthlyWorkloadResponse(
        String username,
        int year,
        int month,
        long totalHours,
        long totalMinutes
) {
}
