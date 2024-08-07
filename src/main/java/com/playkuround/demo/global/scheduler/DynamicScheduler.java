package com.playkuround.demo.global.scheduler;

import com.playkuround.demo.domain.email.service.EmailService;
import com.playkuround.demo.domain.report.service.ReportService;
import com.playkuround.demo.domain.result.service.HealthCheckHttpClient;
import com.playkuround.demo.domain.result.service.ResultService;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import com.playkuround.demo.domain.target.service.TargetService;
import com.playkuround.demo.global.scheduler.runnable.DailyReportSaveAndSendEmailRunnable;
import com.playkuround.demo.global.scheduler.runnable.HealthCheckRunnable;
import com.playkuround.demo.global.scheduler.runnable.ResetTargetSendTodayRunnable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

import java.time.Clock;
import java.time.Duration;

@Component
public class DynamicScheduler {

    private ThreadPoolTaskScheduler scheduler;

    private final HealthCheckRunnable healthCheckRunnable;
    private final ResetTargetSendTodayRunnable resetTargetSendTodayRunnable;
    private final DailyReportSaveAndSendEmailRunnable dailyReportSaveAndSendEmailRunnable;

    private int ms;
    private final String cron;

    public DynamicScheduler(TargetService targetService, ResultService resultService,
                            ReportService reportService, EmailService emailService,
                            TemplateEngine templateEngine, HealthCheckHttpClient httpClient,
                            TargetRepository targetRepository, Clock clock) {
        this.ms = 30000;
        this.cron = "0 0 0 * * ?";

        this.resetTargetSendTodayRunnable = new ResetTargetSendTodayRunnable(targetService);
        this.healthCheckRunnable = new HealthCheckRunnable(clock, resultService, httpClient, targetRepository);
        this.dailyReportSaveAndSendEmailRunnable = new DailyReportSaveAndSendEmailRunnable(clock, emailService, reportService, templateEngine);
        startScheduler();
    }

    private void startScheduler() {
        this.scheduler = new ThreadPoolTaskScheduler();
        this.scheduler.setPoolSize(2);
        this.scheduler.initialize();
        this.scheduler.schedule(dailyReportSaveAndSendEmailRunnable, new CronTrigger(cron));
        this.scheduler.schedule(healthCheckRunnable, new PeriodicTrigger(Duration.ofMillis(ms)));
        this.scheduler.schedule(resetTargetSendTodayRunnable, new CronTrigger("0 0 0 * * ?"));
    }

    public void updateMillisecond(int ms) {
        this.scheduler.shutdown();
        this.ms = ms;
        startScheduler();
    }

    public int getMs() {
        return ms;
    }

    public String getCron() {
        return cron;
    }

}
