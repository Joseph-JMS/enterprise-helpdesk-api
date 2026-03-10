package com.devgroup.enterprise_helpdesk_api.ticket.repository;

import com.devgroup.enterprise_helpdesk_api.category.entity.Category;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.Ticket;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketStatus;
import com.devgroup.enterprise_helpdesk_api.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByCreatedBy(User createdBy, Pageable pageable);
    Page<Ticket> findByAssignedTo(User assignedTo, Pageable pageable);
    Page<Ticket> findByAssignedToIsNullAndStatus(TicketStatus status, Pageable pageable);
    Page<Ticket> findAll(Pageable pageable);
    boolean existsByCategory(Category category);
}
