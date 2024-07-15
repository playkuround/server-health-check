package com.playkuround.demo.domain.result.service;

import com.playkuround.demo.domain.IntegrationTest;
import com.playkuround.demo.domain.email.dto.Mail;
import com.playkuround.demo.domain.email.service.EmailService;
import com.playkuround.demo.domain.result.dto.TargetAndStatus;
import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.result.repository.ResultRepository;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.*;

@IntegrationTest
class ResultServiceTest {

    @MockBean
    private EmailService emailService;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private TargetRepository targetRepository;

    @Autowired
    private ResultService resultService;

    @AfterEach
    void tearDown() {
        resultRepository.deleteAllInBatch();
        targetRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("http 통신 결과와 checkedAt을 받아서 result를 저장하고, target의 최신 상태를 업데이트한다.")
    void organizeHealthCheckResult1() {
        // given
        Target target1 = new Target("host1", "healthCheck1");
        Target target2 = new Target("host2", "healthCheck2");
        targetRepository.saveAll(List.of(target1, target2));

        LocalDateTime checkedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        List<TargetAndStatus> httpResult = List.of(
                new TargetAndStatus(target1, 200),
                new TargetAndStatus(target2, 500)
        );

        // when
        resultService.organizeHealthCheckResult(httpResult, checkedAt);

        // then
        List<Result> results = resultRepository.findAll();
        assertThat(results).hasSize(2)
                .extracting("target.id", "status")
                .containsOnly(
                        tuple(target1.getId(), 200),
                        tuple(target2.getId(), 500)
                );

        List<Target> targets = targetRepository.findAll();
        assertThat(targets).hasSize(2)
                .extracting("id", "lastStatus", "lastCheckedAt")
                .containsOnly(
                        tuple(target1.getId(), 200, checkedAt),
                        tuple(target2.getId(), 500, checkedAt)
                );
    }

    @Test
    @DisplayName("error 이메일 전송 조건에 부합하면, error 이메일을 보내고 target의 todaySend를 true로 변경한다.")
    void organizeHealthCheckResult2() {
        // given
        Target target = spy(new Target("host", "healthCheck"));
        when(target.isNeedToSendErrorEmail()).thenReturn(true);
        targetRepository.save(target);

        LocalDateTime checkedAt = LocalDateTime.now();
        List<TargetAndStatus> httpResult = List.of(new TargetAndStatus(target, 400));

        // when
        resultService.organizeHealthCheckResult(httpResult, checkedAt);

        // then
        List<Target> targets = targetRepository.findAll();
        assertThat(targets).hasSize(1)
                .extracting("todaySend")
                .contains(true);

        verify(emailService, times(1))
                .sendMailAsync(any(Mail.class));
    }

}