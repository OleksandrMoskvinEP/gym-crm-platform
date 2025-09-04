package com.trainersworkloadservice.service;

import com.trainersworkloadservice.model.dto.MonthlyWorkloadRequest;
import com.trainersworkloadservice.model.dto.MonthlyWorkloadResponse;
import com.trainersworkloadservice.model.dto.WorkloadEventRequest;

public interface WorkloadService {
    void calculateAndStoreWorkload(WorkloadEventRequest request);

    MonthlyWorkloadResponse getMonthlyWorkload(MonthlyWorkloadRequest request);
}
