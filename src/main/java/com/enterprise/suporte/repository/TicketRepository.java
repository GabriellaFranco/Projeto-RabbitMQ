package com.enterprise.suporte.repository;

import com.enterprise.suporte.enuns.TicketStatus;
import com.enterprise.suporte.model.SupportAgent;
import com.enterprise.suporte.model.Ticket;
import com.enterprise.suporte.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Override
    Page<Ticket> findAll(Pageable pageable);

    Long countByAgentResponsibleAndStatus(SupportAgent agent, TicketStatus status);
}
