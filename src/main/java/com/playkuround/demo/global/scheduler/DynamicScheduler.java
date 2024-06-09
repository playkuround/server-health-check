package com.playkuround.demo.global.scheduler;

import com.playkuround.demo.domain.email.dto.Mail;
import com.playkuround.demo.domain.email.service.EmailService;
import com.playkuround.demo.domain.report.entity.Report;
import com.playkuround.demo.domain.report.service.ReportService;
import com.playkuround.demo.domain.result.service.ResultService;
import com.playkuround.demo.domain.target.service.TargetService;
import lombok.Getter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;

@Component
public class DynamicScheduler {

    private final EmailService emailService;
    private final TargetService targetService;
    private final ResultService resultService;
    private final ReportService reportService;
    private ThreadPoolTaskScheduler scheduler;

    @Getter
    private int ms;
    @Getter
    private String cron;

    public DynamicScheduler(TargetService targetService, ResultService resultService,
                            ReportService reportService, EmailService emailService) {
        this.targetService = targetService;
        this.resultService = resultService;
        this.reportService = reportService;
        this.emailService = emailService;
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
        return resultService::doHealthCheck;
    }

    private Runnable getResetTargetSendTodayRunnable() {
        return targetService::resetSendToday;
    }

    private Runnable getDailyReportSaveRunnable() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return () -> {
            Collection<Report> reports = reportService.dailySaveReport(yesterday);

            // TODO Template
            String title = "Daily Report";
            StringBuilder contentBody = new StringBuilder();
            contentBody.append("Format : <b>{Host} >> {successCount}, {failCount}, {otherCount}</b><br/>");
            for (Report report : reports) {
                contentBody.append("<b>")
                        .append(report.getTarget().getHost())
                        .append("</b> >> ")
                        .append(report.getSuccessCount())
                        .append(", ")
                        .append(report.getFailCount())
                        .append(", ")
                        .append(report.getOtherCount())
                        .append("<br/>");
            }
            Mail mail = new Mail(title, contentBody.toString());
            emailService.sendMailAsync(mail);
        };
    }

}
