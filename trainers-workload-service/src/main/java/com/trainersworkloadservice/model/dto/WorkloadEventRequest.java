package com.trainersworkloadservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record WorkloadEventRequest(
        @NotBlank String username,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank Boolean isActive,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate trainingDate,
        @Positive int trainingDuration,
        @NotNull ActionType actionType
) {
}
