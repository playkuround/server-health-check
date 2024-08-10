package com.playkuround.demo.domain.result.dto;

import com.playkuround.demo.domain.target.entity.Target;

public record TargetAndStatus(Target target, int status, String errorMessage) {
}
