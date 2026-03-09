package com.devgroup.enterprise_helpdesk_api.ticket.dto;

import com.devgroup.enterprise_helpdesk_api.category.dto.CategoryResponse;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketPriority;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketStatus;

import java.time.Instant;

public class TicketResponse {

    private Long id;
    private String title;
    private String description;
    private TicketPriority priority;
    private TicketStatus status;
    private String createdBy;
    private String assignedTo;
    private CategoryResponse category;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant resolvedAt;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public TicketPriority getPriority() {
        return priority;
    }
    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }
    public TicketStatus getStatus() {
        return status;
    }
    public void setStatus(TicketStatus status) {
        this.status = status;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public String getAssignedTo() {
        return assignedTo;
    }
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
    public CategoryResponse getCategory() {
        return category;
    }
    public void setCategory(CategoryResponse category) {
        this.category = category;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Instant getResolvedAt() {
        return resolvedAt;
    }
    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

}
