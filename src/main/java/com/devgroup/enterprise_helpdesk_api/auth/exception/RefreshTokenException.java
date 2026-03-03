package com.devgroup.enterprise_helpdesk_api.auth.exception;

public class RefreshTokenException extends RuntimeException {
    public RefreshTokenException(String message) {
        super(message);
    }
}
