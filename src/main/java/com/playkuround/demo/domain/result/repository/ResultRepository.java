package com.playkuround.demo.domain.result.repository;

import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.target.entity.Target;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByTargetOrderByCreatedAtDesc(Target target);

    void deleteByTargetId(Long targetId);

    List<Result> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
