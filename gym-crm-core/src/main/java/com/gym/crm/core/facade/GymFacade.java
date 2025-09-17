package com.gym.crm.core.facade;

import com.gym.crm.core.integration.workload.WorkloadServiceClient;
import com.gym.crm.core.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.core.domain.dto.trainee.TraineeDto;
import com.gym.crm.core.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.core.domain.dto.trainer.TrainerCreateRequest;
import com.gym.crm.core.domain.dto.trainer.TrainerDto;
import com.gym.crm.core.domain.dto.trainer.TrainerUpdateRequest;
import com.gym.crm.core.domain.dto.training.TrainingDto;
import com.gym.crm.core.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.core.domain.dto.user.ChangeActivationStatusDto;
import com.gym.crm.core.domain.model.TrainingType;
import com.gym.crm.core.integration.workload.common.PendingTrainingStore;
import com.gym.crm.core.mapper.TraineeMapper;
import com.gym.crm.core.mapper.TrainerMapper;
import com.gym.crm.core.mapper.TrainingMapper;
import com.gym.crm.core.mapper.TrainingTypeMapper;
import com.gym.crm.core.mapper.UserMapper;
import com.gym.crm.core.repository.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.core.repository.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.core.rest.ActivationStatusRequest;
import com.gym.crm.core.rest.AvailableTrainerGetResponse;
import com.gym.crm.core.rest.ChangePasswordRequest;
import com.gym.crm.core.rest.TraineeAssignedTrainersUpdateRequest;
import com.gym.crm.core.rest.TraineeAssignedTrainersUpdateResponse;
import com.gym.crm.core.rest.TraineeCreateResponse;
import com.gym.crm.core.rest.TraineeGetResponse;
import com.gym.crm.core.rest.TraineeTrainingGetResponse;
import com.gym.crm.core.rest.TraineeUpdateResponse;
import com.gym.crm.core.rest.Trainer;
import com.gym.crm.core.rest.TrainerCreateResponse;
import com.gym.crm.core.rest.TrainerGetResponse;
import com.gym.crm.core.rest.TrainerTrainingGetResponse;
import com.gym.crm.core.rest.TrainerUpdateResponse;
import com.gym.crm.core.rest.TrainingCreateRequest;
import com.gym.crm.core.rest.TrainingTypeGetResponse;
import com.gym.crm.core.rest.TrainingWithTraineeName;
import com.gym.crm.core.rest.TrainingWithTrainerName;
import com.gym.crm.core.service.TraineeService;
import com.gym.crm.core.service.TrainerService;
import com.gym.crm.core.service.TrainingService;
import com.gym.crm.core.service.common.UserProfileService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Component
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserProfileService userProfileService;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;
    private final TrainingTypeMapper trainingTypeMapper;
    private final UserMapper userMapper;
    private final WorkloadServiceClient workloadServiceClient;
    private final PendingTrainingStore pendingTrainingStore;

    public TrainerCreateResponse addTrainer(@Valid TrainerCreateRequest createRequest) {
        return trainerMapper.toCreateResponse(trainerService.addTrainer(createRequest));
    }

    public TraineeCreateResponse addTrainee(@Valid TraineeCreateRequest createRequest) {
        return traineeMapper.dtoToCreateResponse(traineeService.addTrainee(createRequest));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<TrainerDto> getAllTrainers() {
        return trainerService.getAllTrainers();
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public List<TraineeDto> getAllTrainees() {
        return traineeService.getAllTrainees();
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public TrainerGetResponse getTrainerByUsername(String username) {
        return trainerMapper.toGetResponse(trainerService.getTrainerByUsername(username));
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public TraineeGetResponse getTraineeByUsername(String username) {
        return traineeMapper.dtoToGetResponse(traineeService.getTraineeByUsername(username));
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public TraineeAssignedTrainersUpdateResponse updateTraineeTrainersList(String username,
                                                                           TraineeAssignedTrainersUpdateRequest request) {
        List<Trainer> trainers = traineeService.updateTraineeTrainersByUsername(username, request.getTrainerUsernames()).stream()
                .map(trainerMapper::entityToRestTrainer).toList();

        return new TraineeAssignedTrainersUpdateResponse(trainers);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINEE')")
    public TraineeUpdateResponse updateTraineeByUsername(String username,
                                                         @Valid TraineeUpdateRequest updateRequest) {
        return traineeMapper.dtoToUpdateResponse(traineeService.updateTraineeByUsername(username, updateRequest));
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public TrainerUpdateResponse updateTrainerByUsername(String username,
                                                         @Valid TrainerUpdateRequest updateRequest) {
        return trainerMapper.toUpdateResponse(trainerService.updateTrainerByUsername(username, updateRequest));
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public void deleteTrainerByUsername(String username) {
        trainerService.deleteTrainerByUsername(username);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINEE')")
    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeByUsername(username);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public TrainerTrainingGetResponse getTrainerTrainingsByFilter(@Valid TrainerTrainingSearchFilter criteria) {
        List<TrainingDto> trainings = trainingService.getTrainerTrainingsByFilter(criteria);

        List<TrainingWithTraineeName> trainingWithTraineeNames = trainings.stream()
                .map(this::buildTrainingWithTraineeName)
                .toList();

        return new TrainerTrainingGetResponse(trainingWithTraineeNames);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','TRAINEE')")
    public TraineeTrainingGetResponse getTraineeTrainingsByFilter(@Valid TraineeTrainingSearchFilter filter) {
        List<TrainingDto> trainings = trainingService.getTraineeTrainingsByFilter(filter);

        List<TrainingWithTrainerName> trainingWithTrainerNames = trainings.stream()
                .map(this::buildTrainingWithTrainerName)
                .toList();

        return new TraineeTrainingGetResponse(trainingWithTrainerNames);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','TRAINEE')")
    public AvailableTrainerGetResponse getUnassignedTrainersByTraineeUsername(String username) {
        List<Trainer> trainers = traineeService.getUnassignedTrainersByTraineeUsername(username).stream()
                .map(trainerMapper::toEntity).toList();

        return new AvailableTrainerGetResponse(trainers);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public List<TrainingDto> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public TrainingDto addTraining(@Valid TrainingCreateRequest request) {
        TrainerDto trainer = trainerService.getTrainerByUsername(request.getTrainerUsername());
        TraineeDto trainee = traineeService.getTraineeByUsername(request.getTraineeUsername());

        String correlationId = workloadServiceClient.callWorkloadServiceAdd(request, trainer);

        TrainingSaveRequest saveRequest = new TrainingSaveRequest();
        saveRequest.setTrainingName(request.getTrainingName());
        saveRequest.setTrainingDate(request.getTrainingDate());
        saveRequest.setTrainingDuration(BigDecimal.valueOf(request.getTrainingDuration()));
        saveRequest.setTrainingTypeName(trainer.getSpecialization().getTrainingTypeName());
        saveRequest.setTraineeId(trainee.getTraineeId());
        saveRequest.setTrainerId(trainer.getTrainerId());

        pendingTrainingStore.put(correlationId, saveRequest);

        TrainingDto trainingDto = new TrainingDto();
        trainingDto.setTrainingName(saveRequest.getTrainingName());
        trainingDto.setTrainingDate(saveRequest.getTrainingDate());
        trainingDto.setTrainingDuration(saveRequest.getTrainingDuration());

        return trainingDto;
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','TRAINEE')")
    public TrainingTypeGetResponse getAllTrainingsTypes() {
        List<TrainingType> trainingTypes = trainingService.getTrainingTypes();
        var trainingTypesRest = trainingTypes.stream().map(trainingTypeMapper::toRestTrainingType).toList();

        return new TrainingTypeGetResponse().trainingTypes(trainingTypesRest);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINEE')")
    public TrainingDto updateTraining(@Valid TrainingSaveRequest updateRequest) {
        return trainingService.updateTraining(updateRequest);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public void switchActivationStatus(String username, ActivationStatusRequest request) {
        ChangeActivationStatusDto changeActivation = userMapper.toChangeActivationStatusDto(username, request);

        userProfileService.switchActivationStatus(changeActivation);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','TRAINEE')")
    public void changePassword(ChangePasswordRequest request) {
        userProfileService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
    }

    private TrainingWithTrainerName buildTrainingWithTrainerName(TrainingDto trainingDto) {
        String trainerName = trainerService.getTrainerNameById(trainingDto.getTrainerId());

        TrainingWithTrainerName trainingWithTrainerName = new TrainingWithTrainerName();

        trainingWithTrainerName.setTrainerName(trainerName);
        trainingWithTrainerName.setTrainingName(trainingDto.getTrainingName());
        trainingWithTrainerName.setTrainingDate(trainingDto.getTrainingDate());
        trainingWithTrainerName.setTrainingType(trainingDto.getTrainingType().getTrainingTypeName());
        trainingWithTrainerName.setTrainingDuration(trainingDto.getTrainingDuration().intValue());

        return trainingWithTrainerName;
    }

    private TrainingWithTraineeName buildTrainingWithTraineeName(TrainingDto trainingDto) {
        String traineeName = traineeService.getTraineeNameById(trainingDto.getTrainerId());

        TrainingWithTraineeName trainingWithTraineeName = new TrainingWithTraineeName();

        trainingWithTraineeName.setTraineeName(traineeName);
        trainingWithTraineeName.setTrainingName(trainingDto.getTrainingName());
        trainingWithTraineeName.setTrainingDate(trainingDto.getTrainingDate());
        trainingWithTraineeName.setTrainingType(trainingDto.getTrainingType().getTrainingTypeName());
        trainingWithTraineeName.setTrainingDuration(trainingDto.getTrainingDuration().intValue());

        return trainingWithTraineeName;
    }
}
