package com.aiproject.auth_service.exception;

public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(message);
    }
}