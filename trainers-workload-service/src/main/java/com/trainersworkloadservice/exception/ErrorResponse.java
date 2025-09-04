package com.trainersworkloadservice.exception;

public record ErrorResponse(
        String code,
        String message
) {
}
