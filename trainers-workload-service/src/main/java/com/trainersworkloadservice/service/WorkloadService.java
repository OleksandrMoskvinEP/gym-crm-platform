package com.trainersworkloadservice.service;

import com.gymcommon.workload.MonthlyWorkloadRequest;
import com.gymcommon.workload.MonthlyWorkloadResponse;
import com.gymcommon.workload.WorkloadEventRequest;

public interface WorkloadService {
    void calculateAndStoreWorkload(WorkloadEventRequest request);

    MonthlyWorkloadResponse getMonthlyWorkload(MonthlyWorkloadRequest request);
}
