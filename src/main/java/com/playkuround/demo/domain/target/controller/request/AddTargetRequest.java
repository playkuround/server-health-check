package com.playkuround.demo.domain.target.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddTargetRequest {

    @NotBlank
    private String host;

    @NotBlank
    private String healthCheckURL;
}
