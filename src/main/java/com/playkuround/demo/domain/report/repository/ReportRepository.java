package com.playkuround.demo.domain.report.repository;

import com.playkuround.demo.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
