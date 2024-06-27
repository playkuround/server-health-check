package com.playkuround.demo.domain.report.entity;

import com.playkuround.demo.domain.common.StatusCheck;
import com.playkuround.demo.domain.target.entity.Target;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;

class ReportTest {

    @Test
    @DisplayName("처음 Report 객체를 생성하면 successCount, failCount, totalCount는 0이다.")
    void constructor() {
        // given
        Target target = new Target("host", "healthCheck");
        LocalDate date = LocalDate.now();

        // when
        Report report = new Report(target, date);

        // then
        assertThat(report)
                .extracting("successCount", "failCount", "otherCount")
                .containsExactly(0, 0, 0);
    }

    @Test
    @DisplayName("인자로 주어지는 status가 StatusCheck.isOK()라면 successCount가 증가한다.")
    void addStatus1() {
        // given
        Target target = new Target("host", "healthCheck");
        LocalDate date = LocalDate.now();
        Report report = new Report(target, date);

        // when
        try (MockedStatic<StatusCheck> utilities = Mockito.mockStatic(StatusCheck.class)) {
            utilities.when(() -> StatusCheck.isOK(anyInt()))
                    .thenReturn(true);
            utilities.when(() -> StatusCheck.isFail(anyInt()))
                    .thenReturn(false);

            report.addStatus(200);
        }

        // then
        assertThat(report)
                .extracting("successCount", "failCount", "otherCount")
                .containsExactly(1, 0, 0);
    }

    @Test
    @DisplayName("인자로 주어지는 status가 StatusCheck.isFail()라면 failCount가 증가한다.")
    void addStatus2() {
        // given
        Target target = new Target("host", "healthCheck");
        LocalDate date = LocalDate.now();
        Report report = new Report(target, date);

        // when
        try (MockedStatic<StatusCheck> utilities = Mockito.mockStatic(StatusCheck.class)) {
            utilities.when(() -> StatusCheck.isOK(anyInt()))
                    .thenReturn(false);
            utilities.when(() -> StatusCheck.isFail(anyInt()))
                    .thenReturn(true);

            report.addStatus(400);
        }

        // then
        assertThat(report)
                .extracting("successCount", "failCount", "otherCount")
                .containsExactly(0, 1, 0);
    }

    @Test
    @DisplayName("인자로 주어지는 status가 StatusCheck.isOK(), StatusCheck.isFail()가 아니라면 otherCount가 증가한다.")
    void addStatus3() {
        // given
        Target target = new Target("host", "healthCheck");
        LocalDate date = LocalDate.now();
        Report report = new Report(target, date);

        // when
        try (MockedStatic<StatusCheck> utilities = Mockito.mockStatic(StatusCheck.class)) {
            utilities.when(() -> StatusCheck.isOK(anyInt()))
                    .thenReturn(false);
            utilities.when(() -> StatusCheck.isFail(anyInt()))
                    .thenReturn(false);

            report.addStatus(100);
        }

        // then
        assertThat(report)
                .extracting("successCount", "failCount", "otherCount")
                .containsExactly(0, 0, 1);
    }

}