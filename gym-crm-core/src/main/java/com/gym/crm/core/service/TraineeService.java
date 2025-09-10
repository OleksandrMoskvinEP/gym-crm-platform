package com.gym.crm.core.service;

import com.gym.crm.core.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.core.domain.dto.trainee.TraineeDto;
import com.gym.crm.core.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.core.domain.dto.trainer.TrainerDto;
import com.gym.crm.core.domain.model.Trainer;

import java.util.List;

public interface TraineeService {
    List<TraineeDto> getAllTrainees();

    TraineeDto getTraineeByUsername(String username);

    TraineeDto addTrainee(TraineeCreateRequest traineeCreateRequest);

    TraineeDto updateTraineeByUsername(String username, TraineeUpdateRequest traineeUpdateRequest);

    void deleteTraineeByUsername(String username);

    List<TrainerDto> getUnassignedTrainersByTraineeUsername(String username);

    List<Trainer> updateTraineeTrainersByUsername(String username, List<String> usernames);

    String getTraineeNameById(Long trainerId);
}
