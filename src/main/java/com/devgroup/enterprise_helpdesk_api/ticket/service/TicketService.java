package com.devgroup.enterprise_helpdesk_api.ticket.service;

import com.devgroup.enterprise_helpdesk_api.category.dto.CategoryResponse;
import com.devgroup.enterprise_helpdesk_api.category.entity.Category;
import com.devgroup.enterprise_helpdesk_api.category.exception.CategoryNotFoundException;
import com.devgroup.enterprise_helpdesk_api.category.repository.CategoryRepository;
import com.devgroup.enterprise_helpdesk_api.ticket.dto.TicketRequest;
import com.devgroup.enterprise_helpdesk_api.ticket.dto.TicketResponse;
import com.devgroup.enterprise_helpdesk_api.ticket.dto.TicketStatusHistoryResponse;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.Ticket;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketStatus;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.TicketStatusHistory;
import com.devgroup.enterprise_helpdesk_api.ticket.exception.InvalidAssignmentException;
import com.devgroup.enterprise_helpdesk_api.ticket.exception.InvalidStatusTransitionException;
import com.devgroup.enterprise_helpdesk_api.ticket.exception.TicketAccessDeniedException;
import com.devgroup.enterprise_helpdesk_api.ticket.exception.TicketNotFoundException;
import com.devgroup.enterprise_helpdesk_api.ticket.repository.TicketRepository;
import com.devgroup.enterprise_helpdesk_api.ticket.repository.TicketStatusHistoryRepository;
import com.devgroup.enterprise_helpdesk_api.user.entity.User;
import com.devgroup.enterprise_helpdesk_api.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TicketService(TicketRepository ticketRepository,
                         TicketStatusHistoryRepository statusHistoryRepository,
                         UserRepository userRepository,
                         CategoryRepository categoryRepository) {
        this.ticketRepository = ticketRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public TicketResponse create(TicketRequest request, String username) {
        User createdBy = getUserOrThrow(username);
        Category category = getCategoryOrThrow(request.getCategoryId());

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setCategory(category);
        ticket.setCreatedBy(createdBy);

        return toResponse(ticketRepository.save(ticket));
    }


    public Page<TicketResponse> findMyTickets(String username, Pageable pageable) {
        User user = getUserOrThrow(username);
        return ticketRepository.findByCreatedBy(user, pageable)
                .map(this::toResponse);
    }
    public Page<TicketResponse> findUnassigned(Pageable pageable) {
        return ticketRepository.findByAssignedToIsNullAndStatus(TicketStatus.OPEN, pageable)
                .map(this::toResponse);
    }

    public Page<TicketResponse> findAssignedToMe(String username, Pageable pageable) {
        User user = getUserOrThrow(username);
        return ticketRepository.findByAssignedTo(user, pageable)
                .map(this::toResponse);
    }

    public Page<TicketResponse> findAll(Pageable pageable) {
        return ticketRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public List<TicketStatusHistoryResponse> findHistoryByTicket(Long ticketId, String username, List<String> roles) {
        Ticket ticket = getTicketOrThrow(ticketId);

        boolean isAdminOrTechnician = roles.contains("ROLE_ADMIN") || roles.contains("ROLE_TECHNICIAN");

        if (!isAdminOrTechnician && !ticket.getCreatedBy().getUsername().equals(username)) {
            throw new TicketAccessDeniedException("No tienes permiso para ver el historial");
        }

        return statusHistoryRepository.findByTicketOrderByChangedAtAsc(ticket)
                .stream()
                .map(this::toHistoryResponse)
                .toList();
    }


    public TicketResponse findById(Long id, String username, List<String> roles) {
        Ticket ticket = getTicketOrThrow(id);

        boolean isAdminOrTechnician = roles.contains("ROLE_ADMIN") || roles.contains("ROLE_TECHNICIAN");

        if (!isAdminOrTechnician && !ticket.getCreatedBy().getUsername().equals(username)) {
            throw new TicketAccessDeniedException("No tienes permiso para ver este ticket");
        }

        return toResponse(ticket);
    }

    @Transactional
    public TicketResponse assign(Long ticketId, String assignedUsername, String requesterUsername, List<String> roles) {
        Ticket ticket = getTicketOrThrow(ticketId);
        boolean isAdmin = roles.contains("ROLE_ADMIN");

        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new InvalidStatusTransitionException("Solo se pueden asignar tickets en estado OPEN");
        }

        if (isAdmin && (assignedUsername == null || assignedUsername.isBlank())) {
            throw new InvalidAssignmentException("Debes especificar un tecnico par asignar");
        }
        String usernameToAssign = isAdmin ? assignedUsername :  requesterUsername;
        User assignedTo = getUserOrThrow(usernameToAssign);

        TicketStatus previousStatus = ticket.getStatus();
        ticket.setAssignedTo(assignedTo);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        ticketRepository.save(ticket);
        saveStatusHistory(ticket, previousStatus, TicketStatus.IN_PROGRESS, assignedTo);

        return toResponse(ticket);
    }

    @Transactional
    public TicketResponse changeStatus(Long ticketId, TicketStatus newStatus, String username, List<String> roles) {
        Ticket ticket = getTicketOrThrow(ticketId);
        User user = getUserOrThrow(username);
        boolean isAdmin = roles.contains("ROLE_ADMIN");
        boolean isTechnician =  roles.contains("ROLE_TECHNICIAN");

        validateStatusTransition(ticket.getStatus(), newStatus, isAdmin, isTechnician);

        TicketStatus previousStatus = ticket.getStatus();
        ticket.setStatus(newStatus);

        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(Instant.now());
        } else if (previousStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(null);
        }

        ticketRepository.save(ticket);
        saveStatusHistory(ticket, previousStatus, newStatus, user);

        return toResponse(ticket);
    }

    @Transactional
    public TicketResponse cancel(Long ticketId, String username, List<String> roles) {
        Ticket ticket = getTicketOrThrow(ticketId);
        User user = getUserOrThrow(username);
        boolean isAdmin = roles.contains("ROLE_ADMIN");

        if (!isAdmin) {
            if (!ticket.getCreatedBy().getUsername().equals(username)) {
                throw new TicketAccessDeniedException("No tienes permiso para cancelar este ticket");
            }
            if (ticket.getStatus() != TicketStatus.OPEN) {
                throw new InvalidStatusTransitionException("Solo puedes cancelar tickets en estado OPEN");
            }
        }

        TicketStatus previousStatus = ticket.getStatus();
        ticket.setStatus(TicketStatus.CANCELLED);

        if (previousStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(null);
        }

        ticketRepository.save(ticket);
        saveStatusHistory(ticket, previousStatus, TicketStatus.CANCELLED, user);

        return toResponse(ticket);
    }


    private void validateStatusTransition(TicketStatus current, TicketStatus newStatus, boolean isAdmin, boolean isTechnician) {
        if (isAdmin) return;

        if (isTechnician) {
            Map<TicketStatus, List<TicketStatus>> allowed = Map.of(
                    TicketStatus.IN_PROGRESS, List.of(TicketStatus.RESOLVED),
                    TicketStatus.RESOLVED, List.of(TicketStatus.IN_PROGRESS)
            );

            List<TicketStatus> allowedTransitions = allowed.getOrDefault(current, List.of());

            if (!allowedTransitions.contains(newStatus)) {
                throw new InvalidStatusTransitionException("Transicion de " + current + " a " + newStatus + " no permitida");
            }
            return;
        }

        if (newStatus == TicketStatus.CLOSED && current == TicketStatus.RESOLVED) {
            return;
        }

        throw new TicketAccessDeniedException("No tienes permiso para cambair el estado del ticket");
    }

    private void saveStatusHistory(Ticket ticket, TicketStatus previous, TicketStatus newStatus, User changedBy) {
        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicket(ticket);
        history.setPreviousStatus(previous);
        history.setNewStatus(newStatus);
        history.setChangedBy(changedBy);
        statusHistoryRepository.save(history);
    }

    private User getUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    private Ticket getTicketOrThrow(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket con id " + id + " no encontrado"));
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Categoria con id " + id + " no encontrada"));
    }

    private TicketResponse toResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setPriority(ticket.getPriority());
        response.setStatus(ticket.getStatus());
        response.setCreatedBy(ticket.getCreatedBy().getUsername());
        response.setAssignedTo(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getUsername() : null);
        response.setCategory(toCategoryResponse(ticket.getCategory()));
        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        response.setResolvedAt(ticket.getResolvedAt());
        return response;
    }

    private CategoryResponse toCategoryResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setEnabled(category.isEnabled());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }

    private TicketStatusHistoryResponse toHistoryResponse(TicketStatusHistory history) {
        TicketStatusHistoryResponse response = new TicketStatusHistoryResponse();
        response.setId(history.getId());
        response.setChangedBy(history.getChangedBy().getUsername());
        response.setPreviousStatus(history.getPreviousStatus());
        response.setNewStatus(history.getNewStatus());
        response.setChangedAt(history.getChangedAt());
        return response;
    }

}
