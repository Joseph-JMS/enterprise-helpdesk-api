package com.devgroup.enterprise_helpdesk_api.ticket.entity;

import com.devgroup.enterprise_helpdesk_api.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "ticket_status_history")
public class TicketStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_by_id")
    private User changeBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus newStatus;

    @Column(nullable = false, updatable = false)
    private Instant changedAt;

    @PrePersist
    protected void onCreate() {
        this.changedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Ticket getTicket() {
        return ticket;
    }
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
    public User getChangeBy() {
        return changeBy;
    }
    public void setChangeBy(User changeBy) {
        this.changeBy = changeBy;
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
