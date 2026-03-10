package com.devgroup.enterprise_helpdesk_api.report.service;

import com.devgroup.enterprise_helpdesk_api.report.SlaThreshold;
import com.devgroup.enterprise_helpdesk_api.report.dto.*;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.Ticket;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketPriority;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketStatus;
import com.devgroup.enterprise_helpdesk_api.ticket.repository.TicketRepository;
import com.devgroup.enterprise_helpdesk_api.user.entity.RoleName;
import com.devgroup.enterprise_helpdesk_api.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Service
public class ReportService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public ReportService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public SummaryResponse getSummary() {
        SummaryResponse response = new SummaryResponse();
        response.setTotalTickets(ticketRepository.count());
        response.setOpen(ticketRepository.countByStatus(TicketStatus.OPEN));
        response.setInProgress(ticketRepository.countByStatus(TicketStatus.IN_PROGRESS));
        response.setResolved(ticketRepository.countByStatus(TicketStatus.RESOLVED));
        response.setClosed(ticketRepository.countByStatus(TicketStatus.CLOSED));
        response.setCancelled(ticketRepository.countByStatus(TicketStatus.CANCELLED));
        return response;
    }

    public List<CategoryReportResponse> getByCategory() {
        return ticketRepository.countGroupedByCategory()
                .stream()
                .map(row -> {
                    CategoryReportResponse response = new CategoryReportResponse();
                    response.setCategoryName(row.getCategoryName());
                    response.setTotalTickets(row.getTotalTickets());
                    return response;
                })
                .toList();
    }

    public List<TechnicianReportResponse> getByTechnician() {
        return userRepository.findByRoles_Name(RoleName.ROLE_TECHNICIAN, Pageable.unpaged())
                .stream()
                .map(technician -> {
                    TechnicianReportResponse response = new TechnicianReportResponse();
                    response.setTechnicianUsername(technician.getUsername());
                    response.setAssignedTickets(ticketRepository.countByAssignedTo(technician));
                    response.setResolvedTickets(ticketRepository.countByAssignedToAndStatus(technician, TicketStatus.RESOLVED));
                    return response;
                })
                .toList();
    }

    public List<PriorityReportResponse> getByPriority() {
        return Arrays.stream(TicketPriority.values())
                .map(priority -> {
                    PriorityReportResponse response = new PriorityReportResponse();
                    response.setPriority(priority.name());
                    response.setTotalTickets(ticketRepository.countByPriority(priority));
                    return response;
                })
                .toList();
    }

    public List<AverageResolutionResponse> getAverageResolutionTime() {
        return Arrays.stream(TicketPriority.values())
                .map(priority -> {
                    List<Ticket> resolvedTickets = ticketRepository
                            .findByPriorityAndResolvedAtIsNotNull(priority);

                    double avgHours = resolvedTickets.stream()
                            .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getResolvedAt()).toSeconds())
                            .average()
                            .orElse(0.0) / 3600;

                    AverageResolutionResponse response = new AverageResolutionResponse();
                    response.setPriority(priority.name());
                    response.setAverageHours(Math.round(avgHours * 100.0) / 100.0);
                    return response;
                })
                .toList();
    }

    public List<SlaReportResponse> getSla() {
        return Arrays.stream(TicketPriority.values())
                .map(priority -> {
                    int thresholdHours = SlaThreshold.getHours(priority);

                    List<Ticket> resolvedTickets = ticketRepository.findByPriorityAndStatusIn(
                            priority, List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED)
                    );

                    long totalTickets = resolvedTickets.size();

                    long withinSla = resolvedTickets.stream()
                            .filter(t -> t.getResolvedAt() != null)
                            .filter(t -> Duration.between(t.getCreatedAt(), t.getResolvedAt()).toHours() <= thresholdHours)
                            .count();

                    long breachedSla = totalTickets - withinSla;
                    double compliance = totalTickets > 0
                            ? (double) withinSla / totalTickets * 100
                            : 0.0;

                    SlaReportResponse response = new SlaReportResponse();
                    response.setPriority(priority.name());
                    response.setThresholdHours(thresholdHours);
                    response.setTotalTickets(totalTickets);
                    response.setWithinSla(withinSla);
                    response.setBreachedSla(breachedSla);
                    response.setCompliancePercentage(Math.round(compliance * 100.0) / 100.0);
                    return response;
                })
                .toList();
    }
}
