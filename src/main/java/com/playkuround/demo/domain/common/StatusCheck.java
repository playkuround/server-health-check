package com.playkuround.demo.domain.common;

public abstract class StatusCheck {

    private StatusCheck() {
    }

    public static boolean isOK(int status) {
        return status / 100 == 2;
    }

    public static boolean isFail(int status) {
        return status / 100 == 4 || status / 100 == 5;
    }
}
