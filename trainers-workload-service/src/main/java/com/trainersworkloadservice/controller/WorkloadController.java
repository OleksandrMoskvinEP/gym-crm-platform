package com.trainersworkloadservice.controller;

import com.trainersworkload.api.TrainersWorkloadApi;
import com.trainersworkload.api.model.MonthlyWorkloadResponse;
import com.trainersworkload.api.model.WorkloadEventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.trainersworkloadservice.controller.ApiConstants.ROOT_PATH;

@RestController
@RequestMapping(ROOT_PATH + "/trainers-workload")
@RequiredArgsConstructor
public class WorkloadController implements TrainersWorkloadApi {
    @Override
    public ResponseEntity<MonthlyWorkloadResponse> getTrainersWorkload(String username, Integer year, Integer month) {
        return null;
    }

    @Override
    public ResponseEntity<Void> upsertMonthlyWorkload(WorkloadEventRequest workloadEventRequest) {
        return null;
    }
}
