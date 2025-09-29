package com.enterprise.suporte.repository;

import com.enterprise.suporte.enuns.UserProfile;
import com.enterprise.suporte.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    Page<User> findAll(Pageable pageable);

    Optional<User> findByUsername(String email);

    List<User> findByProfile(UserProfile profile);
}
