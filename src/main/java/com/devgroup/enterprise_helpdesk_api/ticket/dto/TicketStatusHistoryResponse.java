package com.devgroup.enterprise_helpdesk_api.ticket.dto;

import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketStatus;

import java.time.Instant;

public class TicketStatusHistoryResponse {

    private Long id;
    private String changedBy;
    private TicketStatus previousStatus;
    private TicketStatus newStatus;
    private Instant changedAt;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getChangedBy() {
        return changedBy;
    }
    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
    public TicketStatus getPreviousStatus() {
        return previousStatus;
    }
    public void setPreviousStatus(TicketStatus previousStatus) {
        this.previousStatus = previousStatus;
    }
    public TicketStatus getNewStatus() {
        return newStatus;
    }
    public void setNewStatus(TicketStatus newStatus) {
        this.newStatus = newStatus;
    }
    public Instant getChangedAt() {
        return changedAt;
    }
    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

}
