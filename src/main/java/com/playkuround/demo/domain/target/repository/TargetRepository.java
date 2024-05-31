package com.playkuround.demo.domain.target.repository;

import com.playkuround.demo.domain.target.entity.Target;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TargetRepository extends JpaRepository<Target, Long> {

    boolean existsByHost(String host);

    Optional<Target> findByHost(String host);
}
