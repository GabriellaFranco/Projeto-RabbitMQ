package com.enterprise.suporte.repository;

import com.enterprise.suporte.model.SupportAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportAgentRepository extends JpaRepository<SupportAgent, Long> {
}
