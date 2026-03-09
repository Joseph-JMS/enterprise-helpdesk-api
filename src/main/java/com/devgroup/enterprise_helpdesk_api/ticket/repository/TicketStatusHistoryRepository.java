package com.devgroup.enterprise_helpdesk_api.ticket.repository;

import com.devgroup.enterprise_helpdesk_api.ticket.entity.Ticket;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketStatusHistoryRepository extends JpaRepository<TicketStatusHistory, Long> {
    List<TicketStatusHistory> findByTicketOrderByChangedAtAsc(Ticket ticket);
}
