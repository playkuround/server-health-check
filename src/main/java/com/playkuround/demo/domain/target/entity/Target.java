package com.playkuround.demo.domain.target.entity;

import com.playkuround.demo.domain.common.StatusCheck;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Target {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String host;

    @Column(nullable = false)
    private String healthCheckURL;

    private int lastStatus;
    private LocalDateTime lastCheckedAt;
    private int consecutiveFailCount;
    private boolean todaySend;

    public Target(String host, String healthCheckURL) {
        this.host = host;
        this.healthCheckURL = healthCheckURL;
    }

    public void updateStatus(int status) {
        this.lastStatus = status;
        this.lastCheckedAt = LocalDateTime.now();
        if (StatusCheck.isOK(status)) {
            this.consecutiveFailCount = 0;
        }
        else if (StatusCheck.isFail(status)) {
            this.consecutiveFailCount++;
        }
    }

    public void updateInfo(String host, String healthCheckURL) {
        this.host = host;
        this.healthCheckURL = healthCheckURL;
    }

    public void setTodaySend(boolean send) {
        this.todaySend = send;
    }
}
