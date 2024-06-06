package com.playkuround.demo.domain.result.service;

import com.playkuround.demo.domain.common.FailCountThreshold;
import com.playkuround.demo.domain.email.dto.Mail;
import com.playkuround.demo.domain.email.service.EmailService;
import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.result.repository.ResultRepository;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final TargetRepository targetRepository;
    private final EmailService emailService;

    public List<Result> findByTargetAndDateSorted(Long targetId, LocalDate date) {
        return resultRepository.findByTargetAndDateSorted(targetId, date);
    }

    @Transactional
    public void doHealthCheck() {
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

            if (FailCountThreshold.isOverThreshold(target.getConsecutiveFailCount())
                    && !target.isTodaySend()) {
                errorTargets.add(target);
            }
        }

        if (!errorTargets.isEmpty()) {
            // TODO Template
            String title = "Health Check Error";
            StringBuilder contentBody = new StringBuilder();
            contentBody.append("Health Check Error<br/>");
            for (Target target : errorTargets) {
                contentBody.append(target.getHost())
                        .append(" >> ")
                        .append(target.getHealthCheckURL())
                        .append("<br/>");
                target.setTodaySend(true);
            }
            Mail mail = new Mail(title, contentBody.toString());
            emailService.sendMailAsync(mail);
        }
    }
}
