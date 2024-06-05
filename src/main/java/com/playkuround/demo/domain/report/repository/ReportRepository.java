package com.playkuround.demo.domain.report.repository;

import com.playkuround.demo.domain.report.entity.Report;
import com.playkuround.demo.domain.target.entity.Target;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByTargetOrderByDateDesc(Target target);
}
