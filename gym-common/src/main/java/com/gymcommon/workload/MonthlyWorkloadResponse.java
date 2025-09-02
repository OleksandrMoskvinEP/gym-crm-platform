package com.gymcommon.workload;

public record MonthlyWorkloadResponse(
        String username,
        Short year,
        Short month,
        Long totalHours,
        Long totalMinutes
) {
}
