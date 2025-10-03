package com.enterprise.suporte.repository;

import com.enterprise.suporte.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Override
    Page<Notification> findAll(Pageable pageable);

    Page<Notification> findAllByDestiny(String destiny, Pageable pageable);

    List<Notification> findAllByTicket_Id(Long id);
}
