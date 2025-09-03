package com.trainersworkloadservice.service.impl;

import com.trainersworkloadservice.model.dto.ActionType;
import com.trainersworkloadservice.model.dto.IncreaseWorkloadParams;
import com.trainersworkloadservice.model.dto.MonthlyWorkloadRequest;
import com.trainersworkloadservice.model.dto.MonthlyWorkloadResponse;
import com.trainersworkloadservice.model.dto.WorkloadEventRequest;
import com.trainersworkloadservice.service.WorkloadService;
import com.trainersworkloadservice.service.util.WorkloadChangePersistor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {
    private final WorkloadChangePersistor workloadChangePersistor;

    @Override
    @Transactional
    public void calculateAndStoreWorkload(WorkloadEventRequest req) {
        int year = req.trainingDate().getYear();
        int month = req.trainingDate().getMonthValue();
        long delta = req.trainingDuration();

        if (req.actionType() == ActionType.ADD) {
            workloadChangePersistor.increaseWorkload(new IncreaseWorkloadParams(
                    req.username(),
                    req.firstName(),
                    req.lastName(),
                    req.isActive(),
                    year, month, delta)
            );
        } else if (req.actionType() == ActionType.DELETE) {
            workloadChangePersistor.decreaseWorkload(
                    req.username(),
                    year, month, delta
            );
        } else {
            throw new IllegalArgumentException("Unknown actionType: " + req.actionType());
        }
    }

    @Override
    public MonthlyWorkloadResponse getMonthlyWorkload(MonthlyWorkloadRequest request) {
        Long workHours = workloadChangePersistor.getMonthlyWorkload(
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
