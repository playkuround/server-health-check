package com.playkuround.demo.domain.config.controller.request;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
public class UpdateConfigRequest {

    @Min(10000)
    private int ms;

    @Min(1)
    private int threshold;

    private boolean healthCheckRun;
}
