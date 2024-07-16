package com.playkuround.demo.domain.report.service;

import com.playkuround.demo.domain.IntegrationTest;
import com.playkuround.demo.domain.report.entity.Report;
import com.playkuround.demo.domain.report.repository.ReportRepository;
import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.result.repository.ResultRepository;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@IntegrationTest
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private TargetRepository targetRepository;

    @Autowired
    private ResultRepository resultRepository;

    @AfterEach
    void clean() {
        reportRepository.deleteAllInBatch();
        resultRepository.deleteAllInBatch();
        targetRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("일일 보고서 저장")
    void dailySaveReport() {
        // given
        Target target1 = targetRepository.save(new Target("test1.com", "health-check1"));
        Target target2 = targetRepository.save(new Target("test2.com", "health-check2"));

        LocalDate localDate = LocalDate.of(2024, 6, 8);
        resultRepository.save(new Result(target1, 200, localDate.atTime(0, 0, 0)));
        resultRepository.save(new Result(target1, 201, localDate.atTime(23, 59, 59)));
        resultRepository.save(new Result(target1, 400, localDate.atTime(0, 1, 0)));
        resultRepository.save(new Result(target1, 100, localDate.atTime(0, 2, 0)));
        resultRepository.save(new Result(target2, 200, localDate.atTime(0, 3, 0)));

        // when
        Collection<Report> reports = reportService.dailySaveReport(localDate);

        // then
        assertThat(reports).hasSize(2)
                .extracting("target.id", "date", "successCount", "failCount", "otherCount")
                .containsExactlyInAnyOrder(
                        tuple(target1.getId(), localDate, 2, 1, 1),
                        tuple(target2.getId(), localDate, 1, 0, 0)
                );
    }
}