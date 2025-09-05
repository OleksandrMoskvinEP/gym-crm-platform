package com.trainersworkloadservice.service.impl;

import com.trainersworkloadservice.model.dto.ActionType;
import com.trainersworkloadservice.model.dto.DecreaseWorkloadParams;
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
    public void calculateAndStoreWorkload(WorkloadEventRequest request) {
        if (request.actionType().equals(ActionType.ADD.name())) {
            workloadChangePersistor.increaseWorkload(getIncreaseWorkloadParams(request));
        } else if (request.actionType().equals(ActionType.DELETE.name())) {
            workloadChangePersistor.decreaseWorkload(getdecreaseWorkloadParams(request));
        } else {
            throw new IllegalArgumentException("Unknown actionType: " + request.actionType());
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

    private DecreaseWorkloadParams getdecreaseWorkloadParams(WorkloadEventRequest request) {
        return DecreaseWorkloadParams.builder()
                .username(request.username())
                .workYear(request.trainingDate().getYear())
                .monthOfYear(request.trainingDate().getMonthValue())
                .hoursToRemove(request.trainingDuration())
                .build();
    }

    private IncreaseWorkloadParams getIncreaseWorkloadParams(WorkloadEventRequest request) {
        return IncreaseWorkloadParams.builder()
                .username(request.username())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .active(request.isActive())
                .workYear(request.trainingDate().getYear())
                .monthOfYear(request.trainingDate().getMonthValue())
                .hoursDelta(request.trainingDuration())
                .build();
    }
}
