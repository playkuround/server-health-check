package com.playkuround.demo.domain.email.dto;

import java.nio.charset.StandardCharsets;

public record Mail(
        String title,
        String content,
        String subtype,
        String encoding,
        String fromPersonal,
        String fromAddress
) {
    public Mail(String title, String content) {
        this(title, content, "HTML", StandardCharsets.UTF_8.name(),
                "플레이쿠라운드", "playkuround@gmail.com");
    }
}
