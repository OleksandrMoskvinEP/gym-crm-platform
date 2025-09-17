package com.gym.crm.workload.controller;

import com.gym.crm.workload.exception.ErrorResponse;
import com.gym.crm.workload.model.dto.MonthlyWorkloadRequest;
import com.gym.crm.workload.model.dto.MonthlyWorkloadResponse;
import com.gym.crm.workload.model.dto.WorkloadEventRequest;
import com.gym.crm.workload.service.WorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gym.crm.workload.controller.ApiConstants.ROOT_PATH;

@Slf4j
@RestController
@RequestMapping(ROOT_PATH + "/trainers-workload")
@RequiredArgsConstructor
public class WorkloadController {
    private final WorkloadService workloadService;

    @GetMapping("/{username}/{year}/{month}")
    @Operation(
            summary = "Get monthly workload",
            description = "Retrieve current workload for a trainer by username, year, and month"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Monthly workload found",
                    content = @Content(schema = @Schema(implementation = MonthlyWorkloadResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer or month not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MonthlyWorkloadResponse> getTrainersWorkload(@PathVariable("username") String username,
                                                                       @PathVariable("year") Integer year,
                                                                       @PathVariable("month") Integer month) {
        log.info("getTrainersWorkload for username={}, year={}, month={}", username, year, month);

        return ResponseEntity.ok(workloadService.getMonthlyWorkload(new MonthlyWorkloadRequest(username, year, month)));
    }

//    @PostMapping
//    @Operation(
//            summary = "Update monthly workload",
//            description = "Create or update workload data for a trainer"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Workload updated successfully"),
//            @ApiResponse(responseCode = "400", description = "Validation error",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "500", description = "Unexpected server error",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    public ResponseEntity<Void> updateMonthlyWorkload(@RequestBody @Valid WorkloadEventRequest workloadEventRequest) {
//        log.info("updateMonthlyWorkload for request={}", workloadEventRequest);
//
//        workloadService.calculateAndStoreWorkload(workloadEventRequest);
//        log.info("updating for request={} was successfully", workloadEventRequest);
//
//        return ResponseEntity.ok().build();
//    }
}
