package com.gym.crm.core.domain.dto.trainer;

import com.gym.crm.core.domain.model.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class TrainerDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;
    private TrainingType specialization;
    private Long userId;
    private Long trainerId;
}
