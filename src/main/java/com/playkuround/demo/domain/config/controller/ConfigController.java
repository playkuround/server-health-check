package com.playkuround.demo.domain.config.controller;

import com.playkuround.demo.domain.config.controller.request.UpdateConfigRequest;
import com.playkuround.demo.domain.config.dto.ConfigInformationDto;
import com.playkuround.demo.domain.config.service.ConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @GetMapping("/config")
    public String configForm(Model model) {
        ConfigInformationDto configInformation = configService.getConfigInformation();
        model.addAttribute("threshold", configInformation.failCountThreshold());
        model.addAttribute("ms", configInformation.ms());
        model.addAttribute("cron", configInformation.cron());
        model.addAttribute("running", configInformation.isHealthCheckScheduled());

        return "config/config";
    }

    @PostMapping("/config")
    public String updateConfig(@Valid @ModelAttribute UpdateConfigRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "config/config";
        }
        configService.updateConfig(request.getThreshold(), request.getMs(), request.isHealthCheckRun());
        return "complete";
    }

}
