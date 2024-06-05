package com.playkuround.demo.domain.result.repository;

import com.playkuround.demo.domain.result.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {

    void deleteByTargetId(Long targetId);

    List<Result> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT r FROM Result r " +
            "WHERE r.target.id = :targetId AND FUNCTION('DATE', r.createdAt) = :date " +
            "ORDER BY r.createdAt DESC")
    List<Result> findByTargetAndDateSorted(Long targetId, LocalDate date);
}
