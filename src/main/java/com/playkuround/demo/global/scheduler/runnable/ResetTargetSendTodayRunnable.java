package com.playkuround.demo.global.scheduler.runnable;

import com.playkuround.demo.domain.target.service.TargetService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResetTargetSendTodayRunnable implements Runnable {

    private final TargetService targetService;

    @Override
    public void run() {
        targetService.resetSendToday();
    }
}
