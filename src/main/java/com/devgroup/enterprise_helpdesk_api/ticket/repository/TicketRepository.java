package com.devgroup.enterprise_helpdesk_api.ticket.repository;

import com.devgroup.enterprise_helpdesk_api.category.entity.Category;
import com.devgroup.enterprise_helpdesk_api.report.dto.CategoryCount;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.Ticket;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketPriority;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketStatus;
import com.devgroup.enterprise_helpdesk_api.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByCreatedBy(User createdBy, Pageable pageable);
    Page<Ticket> findByAssignedTo(User assignedTo, Pageable pageable);
    Page<Ticket> findByAssignedToIsNullAndStatus(TicketStatus status, Pageable pageable);
    Page<Ticket> findAll(Pageable pageable);
    boolean existsByCategory(Category category);

    long countByStatus(TicketStatus status);
    long countByPriority(TicketPriority priority);
    long countByAssignedTo(User assignedTo);
    long countByAssignedToAndStatus(User assignedTo, TicketStatus status);
    List<Ticket> findByPriorityAndResolvedAtIsNotNull(TicketPriority priority);
    List<Ticket> findByPriorityAndStatusIn(TicketPriority priority, List<TicketStatus> statuses);

    @Query("SELECT t.category.name AS categoryName, COUNT(t) AS totalTickets FROM Ticket t GROUP BY t.category.name")
    List<CategoryCount> countGroupedByCategory();
}
