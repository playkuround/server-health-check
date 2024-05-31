package com.playkuround.demo.domain.target.controller;

import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.result.service.ResultService;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.service.TargetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}
