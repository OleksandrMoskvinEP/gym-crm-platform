package com.gymcommon.workload;

public record MonthlyWorkloadResponse(
        String username,
        int year,
        int month,
        int totalHours,
        int totalMinutes
) {
}
