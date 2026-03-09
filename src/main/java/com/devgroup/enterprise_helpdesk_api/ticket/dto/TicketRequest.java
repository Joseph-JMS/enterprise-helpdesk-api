package com.devgroup.enterprise_helpdesk_api.ticket.dto;

import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TicketRequest {

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 150, message = "El titulo no puede superar los 150 caracteres")
    private String title;

    @NotBlank(message = "La descripcion es obligatoria")
    private String description;

    @NotNull(message = "La prioridad es obligatoria")
    private TicketPriority priority;

    @NotNull(message = "La categoria es obligatoria")
    private Long categoryId;

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
    public Long getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

}
