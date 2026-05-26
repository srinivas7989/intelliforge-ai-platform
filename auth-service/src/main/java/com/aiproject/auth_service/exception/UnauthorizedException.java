package com.aiproject.auth_service.exception;

public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(message);
    }
}