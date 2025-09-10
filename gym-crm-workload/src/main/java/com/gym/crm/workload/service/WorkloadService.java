package com.gym.crm.workload.service;

import com.gym.crm.workload.model.dto.MonthlyWorkloadRequest;
import com.gym.crm.workload.model.dto.MonthlyWorkloadResponse;
import com.gym.crm.workload.model.dto.WorkloadEventRequest;

public interface WorkloadService {
    void calculateAndStoreWorkload(WorkloadEventRequest request);

    MonthlyWorkloadResponse getMonthlyWorkload(MonthlyWorkloadRequest request);
}
