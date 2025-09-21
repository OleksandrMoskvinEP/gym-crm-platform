package com.gym.crm.workload.service.impl;

import com.gym.crm.workload.model.dto.ActionType;
import com.gym.crm.workload.model.dto.DecreaseWorkloadParams;
import com.gym.crm.workload.model.dto.IncreaseWorkloadParams;
import com.gym.crm.workload.model.dto.MonthlyWorkloadRequest;
import com.gym.crm.workload.model.dto.MonthlyWorkloadResponse;
import com.gym.crm.workload.model.dto.WorkloadEventRequest;
import com.gym.crm.workload.service.WorkloadService;
import com.gym.crm.workload.service.common.MessageValidator;
import com.gym.crm.workload.service.util.WorkloadChangePersistor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadServiceImpl implements WorkloadService {
    private final WorkloadChangePersistor workloadChangePersistor;
    private final MessageValidator messageValidator;

    @Override
    @Transactional
    public void calculateAndStoreWorkload(WorkloadEventRequest request) {
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        messageValidator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            log.error("Validation errors occurred for request {}: {}", request, bindingResult.getAllErrors());

            throw new IllegalArgumentException("Validation failed: " + bindingResult.getAllErrors());
        }

        if (request.actionType().equals(ActionType.ADD.name())) {
            workloadChangePersistor.increaseWorkload(getIncreaseWorkloadParams(request));
        } else if (request.actionType().equals(ActionType.DELETE.name())) {
            workloadChangePersistor.decreaseWorkload(getDecreaseWorkloadParams(request));
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

    private DecreaseWorkloadParams getDecreaseWorkloadParams(WorkloadEventRequest request) {
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
