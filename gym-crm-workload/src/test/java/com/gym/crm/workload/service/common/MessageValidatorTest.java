package com.gym.crm.workload.service.common;

import com.gym.crm.workload.model.dto.WorkloadEventRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class MessageValidatorTest {
    private MessageValidator messageValidator;

    @BeforeEach
    void setUp() {
        messageValidator = new MessageValidator();
    }

    @Test
    void shouldPassWhenRequestIsCorrect() {
        BindingResult actual = new BeanPropertyBindingResult(getCorrectRequest(), "workloadRequest");
        messageValidator.validate(getCorrectRequest(), actual);

        assertThat(actual.hasErrors()).isFalse();
    }

    @Test
    void shouldFailWhenRequestIsInvalid() {
        BindingResult actual = new BeanPropertyBindingResult(getCorrectRequest(), "workloadRequest");
        messageValidator.validate(getInvalidRequest(), actual);

        assertThat(actual.hasErrors()).isTrue();
        assertThat(actual.getErrorCount()).isEqualTo(2);
        assertThat(actual.getFieldError("username")).isNotNull();
        assertThat(Objects.requireNonNull(actual.getFieldError("username")).
                getDefaultMessage()).isEqualTo("Username must not be empty");
        assertThat(actual.getFieldError("actionType")).isNotNull();
        assertThat(Objects.requireNonNull(actual.getFieldError("actionType"))
                .getDefaultMessage()).isEqualTo("Action type must not be empty");

    }

    private WorkloadEventRequest getCorrectRequest() {
        return new WorkloadEventRequest(
                "arnold",
                "Arnold",
                "Schwarzenegger",
                true,
                java.time.LocalDate.now(),
                60,
                "ADD"
        );
    }

    private WorkloadEventRequest getInvalidRequest() {
        return new WorkloadEventRequest(
                "",
                "Arnold",
                "Schwarzenegger",
                true,
                java.time.LocalDate.now(),
                60,
                null
        );
    }
}