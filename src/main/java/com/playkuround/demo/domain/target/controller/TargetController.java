package com.playkuround.demo.domain.target.controller;

import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.result.service.ResultService;
import com.playkuround.demo.domain.target.controller.request.AddTargetRequest;
import com.playkuround.demo.domain.target.controller.request.UpdateTargetRequest;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.exception.TargetDuplicationHostException;
import com.playkuround.demo.domain.target.service.TargetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TargetController {

    private final TargetService targetService;
    private final ResultService resultService;

    @GetMapping("/targets")
    public String targetList(Model model) {
        List<Target> targets = targetService.findAll();
        model.addAttribute("targets", targets);
        return "target/targets";
    }

    @GetMapping("/targets/{targetId}")
    public String targetDetail(@PathVariable Long targetId, Model model) {
        Target target;
        try {
            target = targetService.findById(targetId);
        } catch (Exception e) {
            return "error/404";
        }

        List<Result> results = resultService.findByTargetSorted(target);

        model.addAttribute("target", target);
        model.addAttribute("results", results);

        return "target/detail";
    }

    @GetMapping("/targets/new")
    public String addTargetForm(Model model) {
        model.addAttribute("addTargetRequest", new AddTargetRequest());
        return "target/addTarget";
    }

    @PostMapping("/targets/new")
    public String addTarget(@Valid @ModelAttribute AddTargetRequest request,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "target/addTarget";
        }

        try {
            targetService.addTarget(request.getHost(), request.getHealthCheckURL());
        } catch (TargetDuplicationHostException e) {
            bindingResult.addError(new FieldError("addTargetRequest", "host", "이미 등록된 호스트입니다."));
            return "target/addTarget";
        }

        return "complete";
    }

    @GetMapping("/targets/{targetId}/update")
    public String updateTargetFrom(@PathVariable Long targetId, Model model) {
        Target target;
        try {
            target = targetService.findById(targetId);
        } catch (Exception e) {
            return "error/404";
        }

        UpdateTargetRequest request = new UpdateTargetRequest(target.getHost(), target.getHealthCheckURL());

        model.addAttribute("targetId", target.getId());
        model.addAttribute("updateTargetRequest", request);
        return "target/modify";
    }

    @PostMapping("/targets/{targetId}/update")
    public String updateTarget(@PathVariable Long targetId,
                               @Valid @ModelAttribute UpdateTargetRequest request,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "target/modify";
        }

        try {
            targetService.updateTarget(targetId, request.getHost(), request.getHealthCheckURL());
        } catch (TargetDuplicationHostException e) {
            bindingResult.addError(new FieldError("updateTargetRequest", "host", "이미 등록된 호스트입니다."));
            return "target/modify";
        }

        return "redirect:/targets/" + targetId;
    }
}
