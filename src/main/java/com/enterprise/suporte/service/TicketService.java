package com.enterprise.suporte.service;

import com.enterprise.suporte.dto.ticket.AssignTicketPriority;
import com.enterprise.suporte.dto.ticket.TicketRequestDTO;
import com.enterprise.suporte.dto.ticket.TicketResponseDTO;
import com.enterprise.suporte.dto.ticket.UpdateTicketStatusDTO;
import com.enterprise.suporte.enuns.*;
import com.enterprise.suporte.exception.BusinessException;
import com.enterprise.suporte.exception.ResourceNotFoundException;
import com.enterprise.suporte.mapper.TicketMapper;
import com.enterprise.suporte.model.SupportAgent;
import com.enterprise.suporte.model.Ticket;
import com.enterprise.suporte.repository.CustomerRepository;
import com.enterprise.suporte.repository.SupportAgentRepository;
import com.enterprise.suporte.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SupportAgentRepository supportAgentRepository;
    private final CustomerRepository customerRepository;
    private final AuthenticationService authenticationService;
    private final NotificationService notificationService;
    private final TicketHistoryService ticketHistoryService;
    private final TicketMapper ticketMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public Page<TicketResponseDTO> getAllTickets(Pageable pageable) {
        var tickets = ticketRepository.findAll(pageable);
        return tickets.map(ticketMapper::toTicketResponseDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ATENDENTE')")
    public TicketResponseDTO getTicketById(Long id) {
        return ticketRepository.findById(id).map(ticketMapper::toTicketResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket não encontrado: " + id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE', 'ATENDENTE')")
    public TicketResponseDTO createTicket(TicketRequestDTO ticketDTO) {
        var loggedUser = authenticationService.getLoggedUser();
        var customer = customerRepository.findCustomerByUser_Id(loggedUser.getId());
        var supportAgents = validateAvailabilityOfAgents(UserProfile.ATENDENTE);
        var agentResponsible = validateAgentWithLessTickets(supportAgents);

        var ticket = ticketMapper.toTicket(ticketDTO, customer, agentResponsible);
        ticket.setCustomer(customer);
        ticket.setStatus(TicketStatus.ABERTO);
        ticket.setOpenedAt(LocalDateTime.now());
        var savedTicket = ticketRepository.save(ticket);

        ticketHistoryService.registerHistory(
                savedTicket.getId(), TicketEvent.TICKET_CRIADO, savedTicket.getDescription(), savedTicket.getCustomer().getUser().getId());
        ticketHistoryService.registerHistory(
                savedTicket.getId(), TicketEvent.AGENTE_DESIGNADO, savedTicket.getDescription(), null);
        notificationService.sendNotification(savedTicket.getId(), Channel.EMAIL, savedTicket.getCustomer().getEmail(),
                TicketEvent.TICKET_CRIADO, "Tícket criado", NotificationStatus.PENDENTE);

        return ticketMapper.toTicketResponseDTO(savedTicket);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ATENDENTE', 'SUPERVISOR')")
    public TicketResponseDTO updateTicketStatus(UpdateTicketStatusDTO statusDTO, Long ticketId) {
        var ticket = ticketRepository.findById(ticketId).orElseThrow(
                () -> new ResourceNotFoundException("Ticket não encontrado: " + ticketId));

        validateTicketStatusUpdate(statusDTO, ticket);
        ticketMapper.updateTicketStatus(statusDTO, ticket);
        var savedTicket = ticketRepository.save(ticket);

        ticketHistoryService.registerHistory(savedTicket.getId(), TicketEvent.STATUS_ATUALIZADO,
                "Status atualizado para: " + TicketEvent.STATUS_ATUALIZADO, savedTicket.getAgentResponsible().getUser().getId());
        notificationService.sendNotification(savedTicket.getId(), Channel.EMAIL, savedTicket.getCustomer().getEmail(),
                TicketEvent.STATUS_ATUALIZADO, "Status atualizado para: " + TicketEvent.STATUS_ATUALIZADO, NotificationStatus.PENDENTE);

        return ticketMapper.toTicketResponseDTO(savedTicket);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ATENDENTE', 'SUPERVISOR')")
    public TicketResponseDTO assignTicketPriority(AssignTicketPriority priorityDTO, Long ticketId) {
        var ticket = ticketRepository.findById(ticketId).orElseThrow(
                () -> new ResourceNotFoundException("Ticket não encontrado: " + ticketId));

        validateAssignPriority(ticket.getStatus());
        ticketMapper.assignTicketPriority(priorityDTO, ticket);

        ticketHistoryService.registerHistory(ticket.getId(), TicketEvent.PRIORIDADE_ATUALIZADA,
                "Prioridade atualizada para: " + TicketEvent.PRIORIDADE_ATUALIZADA, ticket.getAgentResponsible().getId());

        var savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toTicketResponseDTO(savedTicket);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTicket(Long id) {
        var ticket = ticketRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Ticket não encontrado: " + id));
        ticketRepository.delete(ticket);
    }

    private void validateTicketStatusUpdate(UpdateTicketStatusDTO statusDTO, Ticket ticket) {
        if (ticket.getStatus().equals(TicketStatus.FECHADO)) {
            throw new BusinessException("Não é possível atualizar o status de um ticket já fechado");
        }

        if (statusDTO.newStatus().equals(TicketStatus.FECHADO)) {
            ticket.setClosedAt(LocalDateTime.now());
        }

        var ticketSaved = ticketRepository.save(ticket);
        ticketMapper.toTicketResponseDTO(ticketSaved);
    }

    private List<SupportAgent> validateAvailabilityOfAgents(UserProfile profile) {
        var agents = supportAgentRepository.findAll();
        if (agents.isEmpty()) {
            throw new BusinessException("Não há agentes disponíveis para atribuir o ticket");
        }

        return agents;
    }

    private SupportAgent validateAgentWithLessTickets(List<SupportAgent> agents) {
        return agents.stream()
                .min(Comparator.comparingLong(agent -> ticketRepository.countByAgentResponsibleAndStatus(agent, TicketStatus.ABERTO)))
                .orElseThrow(() -> new BusinessException("Não há agentes disponíveis no momento"));
    }

    private void validateAssignPriority(TicketStatus status) {
        if (status.equals(TicketStatus.FECHADO)) {
            throw new BusinessException("Não é possível mudar a prioridade de um ticket fechado");
        }
    }
}
