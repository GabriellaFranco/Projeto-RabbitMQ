package com.enterprise.suporte.model;

import com.enterprise.suporte.enuns.Channel;
import com.enterprise.suporte.enuns.NotificationStatus;
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
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(nullable = false)
    private Channel channel;

    @Column(nullable = false)
    private String destiny;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private NotificationStatus status;

    @Column(nullable = false)
    private LocalDateTime sendIn;

}
