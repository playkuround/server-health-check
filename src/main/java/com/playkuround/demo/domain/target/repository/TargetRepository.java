package com.playkuround.demo.domain.target.repository;

import com.playkuround.demo.domain.target.entity.Target;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TargetRepository extends JpaRepository<Target, Long> {

    boolean existsByHost(String host);
}
