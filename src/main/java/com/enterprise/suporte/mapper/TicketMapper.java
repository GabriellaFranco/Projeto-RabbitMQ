package com.enterprise.suporte.mapper;

import com.enterprise.suporte.dto.ticket.AssignTicketPriority;
import com.enterprise.suporte.dto.ticket.TicketRequestDTO;
import com.enterprise.suporte.dto.ticket.TicketResponseDTO;
import com.enterprise.suporte.dto.ticket.UpdateTicketStatusDTO;
import com.enterprise.suporte.enuns.TicketStatus;
import com.enterprise.suporte.model.Customer;
import com.enterprise.suporte.model.Ticket;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class TicketMapper {

    public Ticket toTicket(TicketRequestDTO ticketDTO, Customer customer) {
        return Ticket.builder()
                .title(ticketDTO.title())
                .description(ticketDTO.description())
                .build();
    }

    public TicketResponseDTO toTicketResponseDTO(Ticket ticket) {
        return TicketResponseDTO.builder()
                .id(ticket.getId())
                .customerId(ticket.getCustomer().getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .openedAt(ticket.getOpenedAt())
                .closedAt(ticket.getClosedAt())
                .agentResponsible(TicketResponseDTO.AgentDTO.builder()
                        .id(ticket.getAgentResponsible().getId())
                        .name(ticket.getAgentResponsible().getName())
                        .email(ticket.getAgentResponsible().getEmail())
                        .build())
                .history(ticket.getTicketHistory().stream().map(ticketHistory -> TicketResponseDTO.TicketHistoryDTO.builder()
                        .id(ticketHistory.getId())
                        .previousStatus(ticketHistory.getPreviousStatus())
                        .newStatus(ticketHistory.getCurrentStatus())
                        .updatedAt(ticketHistory.getUpdatedAt())
                        .build()).toList())
                .notifications(ticket.getNotifications().stream().map(notification -> TicketResponseDTO.NotificationDTO.builder()
                        .id(notification.getId())
                        .channel(notification.getChannel())
                        .destiny(notification.getDestiny())
                        .status(notification.getStatus())
                        .message(notification.getMessage())
                        .build()).toList())
                .build();
    }

    public void updateTicketStatus(UpdateTicketStatusDTO updateDTO, Ticket ticket) {
        Optional.ofNullable(updateDTO.newStatus()).ifPresent(ticket::setStatus);
    }

    public void assignTicketPriority(AssignTicketPriority priorityDTO, Ticket ticket) {
        Optional.ofNullable(priorityDTO.priority()).ifPresent(ticket::setPriority);
    }
}
