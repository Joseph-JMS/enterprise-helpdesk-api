package com.devgroup.enterprise_helpdesk_api.ticket.exception;

public class TicketAccessDeniedException extends RuntimeException {
    public TicketAccessDeniedException(String message) {
        super(message);
    }
}
