package com.playkuround.demo.domain.report.service;

import com.playkuround.demo.domain.report.entity.Report;
import com.playkuround.demo.domain.report.repository.ReportRepository;
import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.result.repository.ResultRepository;
import com.playkuround.demo.domain.target.entity.Target;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ResultRepository resultRepository;

    @Transactional
    public void dailySaveReport() {
        LocalDate nowDate = LocalDate.now();
        LocalDate yesterday = nowDate.minusDays(1);
        LocalDateTime startDateTime = nowDate
                .minusDays(1)
                .atStartOfDay();
        LocalDateTime endDateTime = nowDate
                .atStartOfDay()
                .minusNanos(1);
        List<Result> results = resultRepository.findByBetweenCreatedAt(startDateTime, endDateTime);

        Map<Target, Report> reportMap = new HashMap<>();
        for (Result result : results) {
            Target target = result.getTarget();
            Report report = reportMap.get(target);
            if (report == null) {
                report = new Report(target, yesterday);
                reportMap.put(target, report);
            }
            report.addStatus(result.getStatus());
        }

        reportRepository.saveAll(reportMap.values());
    }
}
