package com.devgroup.enterprise_helpdesk_api.category.exception;

public class CategoryInUseException extends RuntimeException {
    public CategoryInUseException(String message) {
        super(message);
    }
}
