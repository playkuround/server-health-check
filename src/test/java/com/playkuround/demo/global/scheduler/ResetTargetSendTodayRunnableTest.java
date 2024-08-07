package com.playkuround.demo.global.scheduler;

import com.playkuround.demo.domain.target.service.TargetService;
import com.playkuround.demo.global.scheduler.runnable.ResetTargetSendTodayRunnable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ResetTargetSendTodayRunnableTest {

    @Test
    @DisplayName("targetService의 resetSendToday 메서드를 실행한다.")
    void resetTargetSendToday() {
        // given
        TargetService targetService = mock(TargetService.class);
        ResetTargetSendTodayRunnable resetTargetSendTodayRunnable = new ResetTargetSendTodayRunnable(targetService);

        // when
        resetTargetSendTodayRunnable.run();

        // then
        verify(targetService, times(1)).resetSendToday();
    }

}