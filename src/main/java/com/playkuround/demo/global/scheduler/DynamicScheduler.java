package com.playkuround.demo.global.scheduler;

import com.playkuround.demo.domain.email.dto.Mail;
import com.playkuround.demo.domain.email.service.EmailService;
import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.result.repository.ResultRepository;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DynamicScheduler {

    private final TargetRepository targetRepository;
    private final ResultRepository resultRepository;
    private final EmailService emailService;

    private int ms;
    private String cron;
    private ThreadPoolTaskScheduler scheduler;

    public DynamicScheduler(TargetRepository targetRepository, ResultRepository resultRepository,
                            EmailService emailService) {
        this.targetRepository = targetRepository;
        this.resultRepository = resultRepository;
        this.emailService = emailService;
        this.cron = "0 0 23 * * ?";
        this.ms = 30000;
        startScheduler();
    }

    public void startScheduler() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.initialize();
        scheduler.schedule(getHealthCheckRunnable(), getPeriodcTrigger());
        scheduler.schedule(getSendEmailRunnable(), getCronTrigger());
    }

    public void stopScheduler() {
        scheduler.shutdown();
    }

    public Runnable getHealthCheckRunnable() {
        return () -> {
            // TODO transactional
            List<Target> targets = targetRepository.findAll();
            RestTemplate restTemplate = new RestTemplateBuilder()
                    .setConnectTimeout(Duration.ofSeconds(3))
                    .setReadTimeout(Duration.ofSeconds(3))
                    .build();

            List<Target> errorTargets = new ArrayList<>();
            for (Target target : targets) {

                int statusCode;
                try {
                    ResponseEntity<String> response
                            = restTemplate.getForEntity(target.getHealthCheckURL(), String.class);
                    statusCode = response.getStatusCode().value();
                } catch (HttpClientErrorException e) {
                    statusCode = 400;
                }

                resultRepository.save(new Result(target, statusCode));
                target.updateStatus(statusCode);
                targetRepository.save(target);

                if (statusCode / 100 == 4 || statusCode / 100 == 5) {
                    errorTargets.add(target);
                }
            }

            if (!errorTargets.isEmpty()) {
                // TODO Template
                String title = "Health Check Error";
                StringBuilder sb = new StringBuilder();
                sb.append("Health Check Error<br/>");
                for (Target target : errorTargets) {
                    sb.append(target.getHost())
                            .append(" >> ")
                            .append(target.getHealthCheckURL())
                            .append("<br/>");
                }
                Mail mail = new Mail(title, sb.toString());
                emailService.sendMail(mail);
            }
        };
    }

    public Runnable getSendEmailRunnable() {
        return () -> {

        };
    }

    private Trigger getCronTrigger() {
        return new CronTrigger(cron);
    }

    public void updateCronSet(String cron) {
        this.cron = cron;
    }

    private Trigger getPeriodcTrigger() {
        return new PeriodicTrigger(ms, TimeUnit.MILLISECONDS);
    }

    public void updateMillisecondSet(int ms) {
        this.ms = ms;
    }
}
