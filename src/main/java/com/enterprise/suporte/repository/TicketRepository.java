package com.enterprise.suporte.repository;

import com.enterprise.suporte.enuns.TicketStatus;
import com.enterprise.suporte.model.Customer;
import com.enterprise.suporte.model.SupportAgent;
import com.enterprise.suporte.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Override
    Page<Ticket> findAll(Pageable pageable);

    Page<Ticket> findAllByCustomerId(Long id, Pageable pageable);

    Page<Ticket> findAllByAgentResponsible_Id(Long id, Pageable pageable);

    Long countByAgentResponsibleAndStatus(SupportAgent agent, TicketStatus status);
}
