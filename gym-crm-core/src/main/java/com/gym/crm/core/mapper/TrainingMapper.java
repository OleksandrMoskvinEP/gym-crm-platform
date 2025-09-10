package com.gym.crm.core.mapper;

import com.gym.crm.core.domain.dto.training.TrainingDto;
import com.gym.crm.core.domain.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingMapper {
    @Mapping(source = "trainee.id", target = "traineeId")
    @Mapping(source = "trainer.id", target = "trainerId")
    TrainingDto toDto(Training training);
}
