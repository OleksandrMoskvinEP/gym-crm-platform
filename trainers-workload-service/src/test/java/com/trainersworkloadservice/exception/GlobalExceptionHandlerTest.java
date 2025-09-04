package com.trainersworkloadservice.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    private static final ErrorResponse VALIDATION_ERROR_RESPONSE = buildValidationResponse();
    private static final ErrorResponse UNHANDLED_ERROR_RESPONSE = buildUnhandledErrorsResponse();

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleBadRequestsExceptions() {
        ResponseEntity<ErrorResponse> entity = exceptionHandler.handleBadRequest(new IllegalArgumentException("veryBadRequestException"));

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertSame("veryBadRequestException", entity.getBody().message());
        assertEquals("BAD_REQUEST", entity.getBody().code());
    }

    @Test
    void shouldHandleAnyExceptions() {
        ResponseEntity<ErrorResponse> entity = exceptionHandler.handleAny(new RuntimeException("someCheckedException"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertSame("Unexpected server error", entity.getBody().message());
        assertEquals(UNHANDLED_ERROR_RESPONSE.code(), entity.getBody().code());
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        when(path.toString()).thenReturn("field");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConstraintViolation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BAD_REQUEST", response.getBody().code());
        assertTrue(response.getBody().message().contains("field: must not be null"));
    }

    @Test
    void shouldReturnValidationError() {
        BindingResult bindingResult = buildBindingResult();

        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> entity = exceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(VALIDATION_ERROR_RESPONSE.message(), entity.getBody().message());
        assertEquals(VALIDATION_ERROR_RESPONSE.code(), entity.getBody().code());
    }

    private BindingResult buildBindingResult() {
        WrongRequest wrongRequest = new WrongRequest("");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<WrongRequest>> violations = validator.validate(wrongRequest);

        BindingResult bindingResult = new BeanPropertyBindingResult(wrongRequest, "WrongRequest");
        violations.forEach(violation -> addErrorToBinding(violation, bindingResult));

        return bindingResult;
    }

    @Getter
    private static class WrongRequest {
        @NotBlank
        private String name;

        public WrongRequest(String name) {
            this.name = name;
        }
    }

    private void addErrorToBinding(ConstraintViolation<WrongRequest> violation, BindingResult bindingResult) {
        String field = violation.getPropertyPath().toString();
        String message = violation.getMessage();

        bindingResult.addError(new FieldError("WrongRequest", field, message));
    }

    private static ErrorResponse buildValidationResponse() {
        String message = "name: must not be blank";

        return new ErrorResponse("BAD_REQUEST", message);
    }

    private static ErrorResponse buildUnhandledErrorsResponse() {
        return new ErrorResponse("INTERNAL_ERROR", "Unexpected error");
    }
}