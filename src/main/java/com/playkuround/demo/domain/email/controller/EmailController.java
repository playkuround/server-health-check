package com.playkuround.demo.domain.email.controller;

import com.playkuround.demo.domain.email.controller.request.AddEmailRequest;
import com.playkuround.demo.domain.email.entity.Email;
import com.playkuround.demo.domain.email.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/emails")
    public String emailList(Model model) {
        List<Email> emails = emailService.findAll();
        model.addAttribute("emails", emails);
        return "email/emails";
    }

    @PostMapping("/emails/new")
    @ResponseBody
    public void addEmail(@Valid @RequestBody AddEmailRequest request) {
        emailService.registerEmail(request.getEmail());
    }

    @PostMapping("/emails/{emailId}/delete")
    @ResponseBody
    public void deleteEmail(@PathVariable Long emailId) {
        emailService.delete(emailId);
    }

}
