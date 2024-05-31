package com.playkuround.demo.domain.target.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTargetRequest {

    @NotBlank
    private String host;

    @NotBlank
    private String healthCheckURL;

}
