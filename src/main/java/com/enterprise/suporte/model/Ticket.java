package com.enterprise.suporte.model;

import com.enterprise.suporte.enuns.TicketPriority;
import com.enterprise.suporte.enuns.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {

    private Long id;
    private String title;
    private String description;
    private TicketPriority priority;
    private TicketStatus status;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    @ManyToOne
    @JoinColumn(name = "support_agent_id")
    @Column(nullable = false)
    private SupportAgent agentResponsible;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @Column(nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "ticket")
    private List<TicketHistory> ticketHistory;

    @OneToMany(mappedBy = "ticket")
    private List<Notification> notifications;

}
