package com.playkuround.demo.domain.common;

public abstract class FailCountThreshold {

    private static int FAIL_COUNT_THRESHOLD = 3;

    private FailCountThreshold() {
    }

    public static boolean isOverThreshold(int failCount) {
        return failCount >= FAIL_COUNT_THRESHOLD;
    }

    public static void updateThreshold(int failCount) {
        FAIL_COUNT_THRESHOLD = failCount;
    }

    public static int getThreshold() {
        return FAIL_COUNT_THRESHOLD;
    }
}
