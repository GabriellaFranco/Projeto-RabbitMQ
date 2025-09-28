package com.enterprise.suporte.model;

import com.enterprise.suporte.enuns.AgentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "support_agents")
public class SupportAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int maxCapacity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AgentStatus status;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private LocalDate createdAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    @Column(nullable = false)
    private User user;

    @OneToMany(mappedBy = "agentResponsible")
    private List<Ticket> tickets;
}
