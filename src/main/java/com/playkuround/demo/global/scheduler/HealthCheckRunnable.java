package com.playkuround.demo.global.scheduler;

import com.playkuround.demo.domain.result.dto.TargetAndStatus;
import com.playkuround.demo.domain.result.service.HealthCheckHttpClient;
import com.playkuround.demo.domain.result.service.ResultService;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class HealthCheckRunnable implements Runnable {

    private final Clock clock;
    private final ResultService resultService;
    private final HealthCheckHttpClient httpClient;
    private final TargetRepository targetRepository;

    @Override
    public void run() {
        List<Target> targets = targetRepository.findAll();
        List<TargetAndStatus> httpResult = httpClient.exchangeHttp(targets);
        LocalDateTime checkedAt = LocalDateTime.now(clock);
        resultService.organizeHealthCheckResult(httpResult, checkedAt);
    }
}
