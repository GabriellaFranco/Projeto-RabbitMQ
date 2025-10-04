package com.enterprise.suporte.model;

import com.enterprise.suporte.enuns.TicketPriority;
import com.enterprise.suporte.enuns.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Nullable
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(nullable = false)
    private LocalDateTime openedAt;

    @Nullable
    @Column(nullable = true)
    private LocalDateTime closedAt;

    @ManyToOne
    @JoinColumn(name = "support_agent_id")
    private SupportAgent agentResponsible;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Builder.Default
    @OneToMany(mappedBy = "ticket")
    private List<TicketHistory> ticketHistory = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "ticket")
    private List<Notification> notifications = new ArrayList<>();

}
