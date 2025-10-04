package com.enterprise.suporte.repository;

import com.enterprise.suporte.model.TicketHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {

    @Override
    Page<TicketHistory> findAll(Pageable pageable);

    List<TicketHistory> findAllByTicketIdOrderByUpdatedAtDesc(Long id);

    List<TicketHistory> findAllByPerformedByIdOrderByUpdatedAtDesc(Long id);
}
