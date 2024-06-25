package com.playkuround.demo.domain.target.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TargetTest {

    @Test
    @DisplayName("target 생성 시 host와 healthCheckURL을 인자로 받고, 그 외는 타입 기본값으로 초기화된다.")
    void construct() {
        // given
        String host = "test_host.com";
        String healthCheckURL = "healthCheckURL";

        // when
        Target target = new Target(host, healthCheckURL);

        // then
        assertThat(target)
                .extracting("host", "healthCheckURL", "lastStatus",
                        "lastCheckedAt", "consecutiveFailCount", "todaySend")
                .containsExactly(host, healthCheckURL, 0, null, 0, false);
    }

    @TestFactory
    @DisplayName("target의 상태 정보 업데이트")
    Collection<DynamicTest> updateStatus() {
        // given
        Target target = new Target("host", "healthCheckURL");

        return List.of(
                DynamicTest.dynamicTest("업데이트할 status가 실패 유형이라면 consecutiveFailCount가 1 증가한다.", () -> {
                    // given
                    int status = 400;
                    LocalDateTime checkedAt = LocalDateTime.now();

                    // when
                    target.updateStatus(status, checkedAt);

                    // then
                    assertThat(target)
                            .extracting("lastStatus", "lastCheckedAt", "consecutiveFailCount")
                            .containsExactly(status, checkedAt, 1);
                }),
                DynamicTest.dynamicTest("target의 status가 성공 유형이라면 consecutiveFailCount를 0으로 초기화한다.", () -> {
                    // given
                    int status = 200;
                    LocalDateTime checkedAt = LocalDateTime.now();

                    // when
                    target.updateStatus(status, checkedAt);

                    // then
                    assertThat(target)
                            .extracting("lastStatus", "lastCheckedAt", "consecutiveFailCount")
                            .containsExactly(status, checkedAt, 0);
                })
        );
    }

    @Test
    @DisplayName("target의 host와 healthCheckURL를 수정한다.")
    void updateInfo() {
        // given
        Target target = new Target("test_host.com", "healthCheckURL");
        String updateHost = "new_host.com";
        String updateHealthCheckURL = "new_healthCheckURL";

        // when
        target.updateInfo(updateHost, updateHealthCheckURL);

        // then
        assertThat(target)
                .extracting("host", "healthCheckURL")
                .containsExactly(updateHost, updateHealthCheckURL);
    }

    @Test
    @DisplayName("target의 todaySend 필드를 true로 변경한다.")
    void markTodaySendEmail() {
        // given
        Target target = new Target("test_host.com", "healthCheckURL");

        // when
        target.markTodaySendEmail();

        // then
        assertThat(target.isTodaySend()).isTrue();
    }

}