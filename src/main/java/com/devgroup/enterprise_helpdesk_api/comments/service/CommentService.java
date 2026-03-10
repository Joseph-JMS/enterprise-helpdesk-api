package com.devgroup.enterprise_helpdesk_api.comments.service;

import com.devgroup.enterprise_helpdesk_api.comments.dto.CommentRequest;
import com.devgroup.enterprise_helpdesk_api.comments.dto.CommentResponse;
import com.devgroup.enterprise_helpdesk_api.comments.entity.Comment;
import com.devgroup.enterprise_helpdesk_api.comments.exception.CommentAccessDeniedException;
import com.devgroup.enterprise_helpdesk_api.comments.exception.CommentNotFoundException;
import com.devgroup.enterprise_helpdesk_api.comments.repository.CommentRepository;
import com.devgroup.enterprise_helpdesk_api.ticket.entity.Ticket;
import com.devgroup.enterprise_helpdesk_api.ticket.exception.TicketNotFoundException;
import com.devgroup.enterprise_helpdesk_api.ticket.repository.TicketRepository;
import com.devgroup.enterprise_helpdesk_api.user.entity.User;
import com.devgroup.enterprise_helpdesk_api.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, TicketRepository ticketRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CommentResponse create(Long ticketId, CommentRequest request, String username) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket con id " + ticketId + " no encontrado"));

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        Comment comment = new Comment();
        comment.setContent(request.getComtent());
        comment.setTicket(ticket);
        comment.setAuthor(author);

        return toResponse(commentRepository.save(comment));
    }

    @Transactional
    public List<CommentResponse> findByTicket(Long ticketId, String username, List<String> roles) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new TicketNotFoundException("Ticket con id " + ticketId + " no encontrado");
        }

        if (!roles.contains("ROLE_ADMIN") && !roles.contains("ROLE_TECHNICIAN")) {
            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new TicketNotFoundException("Ticket con id " + ticketId + " no encontrado"));
            if (!ticket.getCreatedBy().getUsername().equals(username)) {
                throw new CommentAccessDeniedException("No tienes acceso a los comentarios de este ticket");
            }
        }

        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long ticketId, Long commentId, String username, List<String> roles) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new TicketNotFoundException("Ticket con id " + ticketId + " no encontrado");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comentario con id " + commentId + "no encomtrado"));

        boolean isAdmin = roles.contains("ROLE_ADMIN");
        boolean isAuthor = comment.getAuthor().getUsername().equals(username);

        if (!isAdmin && !isAuthor) {
            throw new CommentAccessDeniedException("No tienes permiso para eliminar este comentario");
        }

        commentRepository.delete(comment);
    }


    private CommentResponse toResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setAuthorUsername(comment.getAuthor().getUsername());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }

}
