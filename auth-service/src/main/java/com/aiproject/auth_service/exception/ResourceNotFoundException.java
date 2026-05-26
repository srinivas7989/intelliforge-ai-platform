package com.aiproject.auth_service.exception;

public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}