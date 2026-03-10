package com.devgroup.enterprise_helpdesk_api.comments.exception;

public class CommentAccessDeniedException extends RuntimeException {
    public CommentAccessDeniedException(String message) {
        super(message);
    }
}
