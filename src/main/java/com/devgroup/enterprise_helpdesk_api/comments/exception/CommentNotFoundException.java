package com.devgroup.enterprise_helpdesk_api.comments.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
