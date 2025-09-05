package com.trainersworkloadservice.controller;

import com.trainersworkloadservice.model.dto.MonthlyWorkloadResponse;
import com.trainersworkloadservice.model.dto.WorkloadEventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.trainersworkloadservice.controller.ApiConstants.ROOT_PATH;

@RestController
@RequestMapping(ROOT_PATH + "/trainers-workload")
@RequiredArgsConstructor
public class WorkloadController{

    @GetMapping
    public ResponseEntity<MonthlyWorkloadResponse> getTrainersWorkload(String username, Integer year, Integer month) {
        return null;
    }

   @PostMapping
    public ResponseEntity<Void> upsertMonthlyWorkload(WorkloadEventRequest workloadEventRequest) {
        return null;
    }
}
