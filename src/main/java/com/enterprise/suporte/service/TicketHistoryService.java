package com.enterprise.suporte.service;

import com.enterprise.suporte.configuration.rabbit.RabbitConfig;
import com.enterprise.suporte.dto.queue.TicketHistoryEventDTO;
import com.enterprise.suporte.dto.tickethistory.TicketHistoryResponseDTO;
import com.enterprise.suporte.enuns.TicketEvent;
import com.enterprise.suporte.exception.ResourceNotFoundException;
import com.enterprise.suporte.mapper.TicketHistoryMapper;
import com.enterprise.suporte.model.TicketHistory;
import com.enterprise.suporte.model.User;
import com.enterprise.suporte.repository.TicketHistoryRepository;
import com.enterprise.suporte.repository.TicketRepository;
import com.enterprise.suporte.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TicketHistoryService {

    private final TicketHistoryRepository ticketHistoryRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketHistoryMapper ticketHistoryMapper;
    private final AmqpTemplate amqpTemplate;

    @RabbitListener(queues = RabbitConfig.TICKET_HISTORY_QUEUE)
    @Transactional
    public void consume(TicketHistoryEventDTO event) {
        var ticket = ticketRepository.findById(event.ticketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket não encontrado: " + event.ticketId()));

        User performedBy = null;
        if (event.performedBy() != null) {
            performedBy = userRepository.findById(event.performedBy())
                    .orElse(null);
        }

        var history = TicketHistory.builder()
                .ticket(ticket)
                .event(event.event())
                .currentStatus(ticket.getStatus())
                .description(event.description())
                .performedBy(performedBy)
                .updatedAt(LocalDateTime.now())
                .build();

        ticketHistoryRepository.save(history);
    }

    @Transactional
    public void registerHistory(Long ticketId, TicketEvent event, String description, Long performedById) {
        TicketHistoryEventDTO dto = new TicketHistoryEventDTO(
                ticketId,
                event,
                description,
                performedById
        );

        amqpTemplate.convertAndSend(
                RabbitConfig.TICKET_HISTORY_EXCHANGE,
                RabbitConfig.TICKET_HISTORY_ROUTING_KEY,
                dto,
                message -> {
                    message.getMessageProperties().setContentType("application/json");
                    return message;
                }
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public Page<TicketHistoryResponseDTO> getAllTicketHistories(Pageable pageable) {
        var histories = ticketHistoryRepository.findAll(pageable);
        return histories.map(ticketHistoryMapper::toTicketHistoryResponseDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    public List<TicketHistoryResponseDTO> getHistoryFromTicket(Long id) {
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket não encontrado: " + id));

        var history = ticketHistoryRepository.findAllByTicketIdOrderByUpdatedAtDesc(id);
        return history.stream().map(ticketHistoryMapper::toTicketHistoryResponseDTO).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public List<TicketHistoryResponseDTO> getHistoryByUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));

        var history = ticketHistoryRepository.findAllByPerformedByIdOrderByUpdatedAtDesc(id);
        return history.stream().map(ticketHistoryMapper::toTicketHistoryResponseDTO).toList();
    }

}
