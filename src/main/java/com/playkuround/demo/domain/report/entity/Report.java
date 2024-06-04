package com.playkuround.demo.domain.report.entity;

import com.playkuround.demo.domain.common.BaseTimeEntity;
import com.playkuround.demo.domain.common.StatusCheck;
import com.playkuround.demo.domain.target.entity.Target;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private Target target;

    @Column(nullable = false)
    private LocalDate date;

    private int successCount;
    private int failCount;
    private int otherCount;

    public Report(Target target, LocalDate date) {
        this.target = target;
        this.date = date;
    }

    public void addStatus(int status) {
        if (StatusCheck.isOK(status)) {
            this.successCount++;
        }
        else if (StatusCheck.isFail(status)) {
            this.failCount++;
        }
        else {
            this.otherCount++;
        }
    }
}
