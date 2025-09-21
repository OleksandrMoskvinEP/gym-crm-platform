package com.gym.crm.workload.service.common;

import com.gym.crm.workload.model.dto.WorkloadEventRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@Slf4j
public class MessageValidator implements Validator {

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return WorkloadEventRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        WorkloadEventRequest request = (WorkloadEventRequest) target;

        checkCorrectness(request.username(), "username", "username.empty",
                "Username must not be empty", errors);
        checkCorrectness(request.firstName(), "firstName", "firstName.empty",
                "First name must not be empty", errors);
        checkCorrectness(request.lastName(), "lastName", "lastName.empty",
                "Last name must not be empty", errors);
        checkCorrectness(request.actionType(), "actionType", "actionType.empty",
                "Action type must not be empty", errors);
        checkCorrectness(request.trainingDate(), "trainingDate", "trainingDate.empty",
                "Training date must not be empty", errors);
        checkCorrectness(request.trainingDuration(), "trainingDuration", "trainingDuration.empty",
                "Training duration must not be empty", errors);
        checkCorrectness(request.isActive(), "isActive", "isActive.empty",
                "Active status must not be empty", errors);
    }

    private void checkCorrectness(Object target, String fieldName, String errorCode, String defaultMessage, Errors errors) {
        if (target == null || (target instanceof String s && s.isBlank())) {
            errors.rejectValue(fieldName, errorCode, defaultMessage);

            log.error("Validation error on field {}: {}", fieldName, defaultMessage);
        }
    }
}