package com.trainersworkloadservice.service.impl;

import com.gymcommon.workload.ActionType;
import com.gymcommon.workload.MonthlyWorkloadRequest;
import com.gymcommon.workload.MonthlyWorkloadResponse;
import com.gymcommon.workload.WorkloadEventRequest;
import com.trainersworkloadservice.service.WorkloadService;
import com.trainersworkloadservice.service.util.WorkloadCalculateHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {
    private final WorkloadCalculateHelper workloadCalculateHelper;

    @Override
    @Transactional
    public void calculateAndStoreWorkload(WorkloadEventRequest req) {
        short year = (short) req.trainingDate().getYear();
        short month = (short) req.trainingDate().getMonthValue();
        long delta = req.trainingDuration();

        if (req.actionType() == ActionType.ADD) {
            workloadCalculateHelper.addOrIncrementWorkload(
                    req.username(),
                    req.firstName(),
                    req.lastName(),
                    req.isActive(),
                    year, month, delta
            );
        } else if (req.actionType() == ActionType.DELETE) {
            workloadCalculateHelper.deleteOrDecrementWorkload(
                    req.username(),
                    year, month, delta
            );
        } else {
            throw new IllegalArgumentException("Unknown actionType: " + req.actionType());
        }
    }

    @Override
    public MonthlyWorkloadResponse getMonthlyWorkload(MonthlyWorkloadRequest request) {
        Long workHours = workloadCalculateHelper.getMonthlyWorkload(
                request.username(),
                request.year(),
                request.month()
        );

        if (workHours != null) {
            return new MonthlyWorkloadResponse(request.username(), request.year(), request.month(), workHours, workHours * 60);
        }

        return null;
    }
}
