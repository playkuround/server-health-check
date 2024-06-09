package com.playkuround.demo.domain.report.service;

import com.playkuround.demo.domain.report.entity.Report;
import com.playkuround.demo.domain.report.repository.ReportRepository;
import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.result.repository.ResultRepository;
import com.playkuround.demo.domain.target.entity.Target;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ResultRepository resultRepository;

    @Transactional
    public Collection<Report> dailySaveReport(LocalDate date) {
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = startDateTime.plusDays(1).minusNanos(1);
        List<Result> results = resultRepository.findByCreatedAtBetween(startDateTime, endDateTime);

        Collection<Report> reports = createReport(date, results);
        reportRepository.saveAll(reports);

        log.info("DailySaveReport. startDateTime={}, endDateTime={}, resultsSize={}, reportsSize={}",
                startDateTime, endDateTime, results.size(), reports.size());
        return reports;
    }

    private Collection<Report> createReport(LocalDate date, List<Result> results) {
        Map<Target, Report> reportMap = new HashMap<>();
        for (Result result : results) {
            Target target = result.getTarget();
            Report report = reportMap.get(target);
            if (report == null) {
                report = new Report(target, date);
                reportMap.put(target, report);
            }
            report.addStatus(result.getStatus());
        }
        return reportMap.values();
    }

    public List<Report> findByTargetSorted(Target target) {
        return reportRepository.findByTargetOrderByDateDesc(target);
    }
}
