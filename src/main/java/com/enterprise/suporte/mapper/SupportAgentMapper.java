package com.enterprise.suporte.mapper;

import com.enterprise.suporte.dto.supportagent.SupportAgentRequestDTO;
import com.enterprise.suporte.dto.supportagent.SupportAgentResponseDTO;
import com.enterprise.suporte.model.SupportAgent;
import org.springframework.stereotype.Component;

@Component
public class SupportAgentMapper {

    public SupportAgent toSupportAgent(SupportAgentRequestDTO supportAgentDTO) {
        return SupportAgent.builder()
                .name(supportAgentDTO.name())
                .email(supportAgentDTO.email())
                .password(supportAgentDTO.password())
                .maxCapacity(supportAgentDTO.maxCapacity())
                .build();
    }

    public SupportAgentResponseDTO toSupportAgentResponseDTO(SupportAgent supportAgent) {
        return SupportAgentResponseDTO.builder()
                .id(supportAgent.getId())
                .name(supportAgent.getName())
                .email(supportAgent.getEmail())
                .maxCapacity(supportAgent.getMaxCapacity())
                .status(supportAgent.getStatus())
                .isActive(supportAgent.getIsActive())
                .createdAt(supportAgent.getCreatedAt())
                .tickets(supportAgent.getTickets().stream().map(ticket -> SupportAgentResponseDTO.TicketDTO.builder()
                        .id(ticket.getId())
                        .title(ticket.getTitle())
                        .status(ticket.getStatus())
                        .priority(ticket.getPriority())
                        .build()).toList())
                .build();
    }
}
