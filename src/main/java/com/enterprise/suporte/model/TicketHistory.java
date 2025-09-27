package com.enterprise.suporte.model;

import com.enterprise.suporte.enuns.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor @NoArgsConstructor
@Entity
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    @Column(nullable = false)
    private Ticket ticket;

    @Column(nullable = false)
    private TicketStatus previousStatus;

    @Column(nullable = false)
    private TicketStatus currentStatus;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String description;
}
