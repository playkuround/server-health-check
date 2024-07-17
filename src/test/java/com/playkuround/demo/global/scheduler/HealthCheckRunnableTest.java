package com.playkuround.demo.global.scheduler;

import com.playkuround.demo.domain.result.dto.TargetAndStatus;
import com.playkuround.demo.domain.result.service.HealthCheckHttpClient;
import com.playkuround.demo.domain.result.service.ResultService;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.Mockito.*;

class HealthCheckRunnableTest {

    @Test
    @DisplayName("target과 http 통신한 결과를 받아서 organizeHealthCheckResult를 실행한다.")
    void healthCheck() {
        // given
        List<Target> targets = List.of(
                new Target("host1", "healthCheck1"),
                new Target("host2", "healthCheck2")
        );
        TargetRepository targetRepository = mock(TargetRepository.class);
        when(targetRepository.findAll()).thenReturn(targets);

        List<TargetAndStatus> httpResult = List.of(
                new TargetAndStatus(targets.get(0), 200),
                new TargetAndStatus(targets.get(1), 500)
        );
        HealthCheckHttpClient httpClient = mock(HealthCheckHttpClient.class);
        when(httpClient.exchangeHttp(targets)).thenReturn(httpResult);

        Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        ResultService resultService = mock(ResultService.class);

        HealthCheckRunnable healthCheckRunnable = new HealthCheckRunnable(clock, resultService, httpClient, targetRepository);

        // when
        healthCheckRunnable.run();

        // then
        verify(resultService, times(1))
                .organizeHealthCheckResult(httpResult, LocalDateTime.now(clock));
    }

}