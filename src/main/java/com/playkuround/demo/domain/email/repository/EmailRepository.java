package com.playkuround.demo.domain.email.repository;

import com.playkuround.demo.domain.email.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {

    boolean existsByEmail(String email);
}
