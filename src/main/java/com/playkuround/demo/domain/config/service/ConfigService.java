package com.playkuround.demo.domain.config.service;

import com.playkuround.demo.domain.common.FailCountThreshold;
import com.playkuround.demo.domain.config.dto.ConfigInformationDto;
import com.playkuround.demo.global.scheduler.DynamicScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final DynamicScheduler scheduler;

    public ConfigInformationDto getConfigInformation() {
        int threshold = FailCountThreshold.getThreshold();
        int schedulerMs = scheduler.getMs();
        String schedulerCron = scheduler.getCron();
        boolean healthCheckScheduled = scheduler.isHealthCheckScheduled();

        return new ConfigInformationDto(threshold, schedulerMs, schedulerCron, healthCheckScheduled);
    }

    public void updateConfig(int threshold, int ms, boolean healthCheckScheduleRun) {
        FailCountThreshold.updateThreshold(threshold);
        scheduler.updateMillisecond(ms);
        scheduler.healthCheckScheduled(healthCheckScheduleRun);
    }
}
