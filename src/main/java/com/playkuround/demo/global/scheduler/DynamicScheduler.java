package com.playkuround.demo.global.scheduler;

import com.playkuround.demo.domain.email.dto.Mail;
import com.playkuround.demo.domain.email.service.EmailService;
import com.playkuround.demo.domain.report.entity.Report;
import com.playkuround.demo.domain.report.service.ReportService;
import com.playkuround.demo.domain.result.dto.TargetAndStatus;
import com.playkuround.demo.domain.result.service.HealthCheckHttpClient;
import com.playkuround.demo.domain.result.service.ResultService;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import com.playkuround.demo.domain.target.service.TargetService;
import lombok.Getter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Component
public class DynamicScheduler {

    private final EmailService emailService;
    private final TargetService targetService;
    private final ResultService resultService;
    private final ReportService reportService;
    private final TemplateEngine templateEngine;
    private final HealthCheckHttpClient httpClient;
    private final TargetRepository targetRepository;
    private ThreadPoolTaskScheduler scheduler;

    @Getter
    private int ms;
    @Getter
    private String cron;

    public DynamicScheduler(TargetService targetService, ResultService resultService,
                            ReportService reportService, EmailService emailService,
                            TemplateEngine templateEngine, HealthCheckHttpClient httpClient, TargetRepository targetRepository) {
        this.targetService = targetService;
        this.resultService = resultService;
        this.reportService = reportService;
        this.emailService = emailService;
        this.templateEngine = templateEngine;
        this.httpClient = httpClient;
        this.targetRepository = targetRepository;
        this.ms = 30000;
        this.cron = "0 0 0 * * ?";
        startScheduler();
    }

    private void startScheduler() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.initialize();
        scheduler.schedule(getDailyReportSaveRunnable(), new CronTrigger(cron));
        scheduler.schedule(getHealthCheckRunnable(), new PeriodicTrigger(Duration.ofMillis(ms)));
        scheduler.schedule(getResetTargetSendTodayRunnable(), new CronTrigger("0 0 0 * * ?"));
    }

    public void updateMillisecond(int ms) {
        scheduler.shutdown();
        this.ms = ms;
        startScheduler();
    }

    private Runnable getHealthCheckRunnable() {
        return () -> {
            List<Target> targets = targetRepository.findAll();
            List<TargetAndStatus> httpResult = httpClient.exchangeHttp(targets);
            LocalDateTime checkedAt = LocalDateTime.now();
            resultService.organizeHealthCheckResult(httpResult, checkedAt);
        };
    }

    private Runnable getResetTargetSendTodayRunnable() {
        return targetService::resetSendToday;
    }

    private Runnable getDailyReportSaveRunnable() {
        return () -> {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            Collection<Report> reports = reportService.dailySaveReport(yesterday);

            String title = "Daily Report";
            List<DailyReportContent> dailyReportContents = reports.stream()
                    .map(report -> new DailyReportContent(
                            report.getTarget().getHost(),
                            report.getSuccessCount(),
                            report.getFailCount(),
                            report.getOtherCount())
                    )
                    .toList();

            Context context = new Context();
            context.setVariable("dailyReportContents", dailyReportContents);
            String content = templateEngine.process("email/dailyReport-template", context);

            Mail mail = new Mail(title, content);
            emailService.sendMailAsync(mail);
        };
    }

}
