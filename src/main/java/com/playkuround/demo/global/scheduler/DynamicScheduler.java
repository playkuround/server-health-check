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
import java.util.concurrent.ScheduledFuture;

@Component
public class DynamicScheduler {

    private ThreadPoolTaskScheduler scheduler;

    private final HealthCheckRunnable healthCheckRunnable;
    private final ResetTargetSendTodayRunnable resetTargetSendTodayRunnable;
    private final DailyReportSaveAndSendEmailRunnable dailyReportSaveAndSendEmailRunnable;

    private int ms;
    private final String cron;
    private ScheduledFuture<?> healthCheckScheduledFuture;

    public DynamicScheduler(TargetService targetService, ResultService resultService,
                            ReportService reportService, EmailService emailService,
                            TemplateEngine templateEngine, HealthCheckHttpClient httpClient,
                            TargetRepository targetRepository, Clock clock) {
        this.ms = 30000;
        this.cron = "0 0 0 * * ?";

        this.resetTargetSendTodayRunnable = new ResetTargetSendTodayRunnable(targetService);
        this.healthCheckRunnable = new HealthCheckRunnable(clock, resultService, httpClient, targetRepository);
        this.dailyReportSaveAndSendEmailRunnable = new DailyReportSaveAndSendEmailRunnable(clock, emailService, reportService, templateEngine);
        this.startScheduler();
    }

    private synchronized void startScheduler() {
        this.scheduler = new ThreadPoolTaskScheduler();
        this.scheduler.setPoolSize(2);
        this.scheduler.initialize();

        this.scheduler.schedule(dailyReportSaveAndSendEmailRunnable, new CronTrigger(cron));
        this.scheduler.schedule(resetTargetSendTodayRunnable, new CronTrigger("0 0 0 * * ?"));

        this.healthCheckScheduledFuture = this.scheduler.schedule(healthCheckRunnable, new PeriodicTrigger(Duration.ofMillis(ms)));
    }

    public synchronized void healthCheckScheduled(boolean schedule) {
        if (schedule == isHealthCheckScheduled()) {
            return;
        }

        if (schedule) {
            healthCheckScheduledFuture = scheduler.schedule(healthCheckRunnable, new PeriodicTrigger(Duration.ofMillis(ms)));
        }
        else {
            healthCheckScheduledFuture.cancel(false);
        }
    }

    public synchronized void updateMillisecond(int ms) {
        if (this.ms == ms) {
            return;
        }

        this.ms = ms;
        if (isHealthCheckScheduled()) {
            healthCheckScheduledFuture.cancel(false);
            healthCheckScheduledFuture = scheduler.schedule(healthCheckRunnable, new PeriodicTrigger(Duration.ofMillis(ms)));
        }
    }

    public boolean isHealthCheckScheduled() {
        return healthCheckScheduledFuture != null && !healthCheckScheduledFuture.isCancelled();
    }

    public int getMs() {
        return ms;
    }

    public String getCron() {
        return cron;
    }

}
