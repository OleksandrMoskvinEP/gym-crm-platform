package com.gym.crm.core.domain.dto.training;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingIdentityDto {
    private Long trainerId;
    private Long traineeId;
    private LocalDate trainingDate;
}
