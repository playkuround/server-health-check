package com.playkuround.demo.domain.result.entity;

import com.playkuround.demo.domain.common.BaseTimeEntity;
import com.playkuround.demo.domain.target.entity.Target;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Result extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private Target target;

    @Column(nullable = false)
    private String healthCheckURL;

    private int status;

    public Result(Target target, int status) {
        this.target = target;
        this.status = status;
        this.healthCheckURL = target.getHealthCheckURL();
    }
}
