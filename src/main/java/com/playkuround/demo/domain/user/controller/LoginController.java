package com.playkuround.demo.domain.user.controller;

import com.playkuround.demo.domain.user.controller.request.LoginRequest;
import com.playkuround.demo.domain.user.service.UserLoginService;
import com.playkuround.demo.global.interceptor.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserLoginService userLoginService;

    @GetMapping("/login")
    public String loginAdminForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "user/login";
    }

    @PostMapping("/login")
    public String loginAdmin(@Valid @ModelAttribute LoginRequest request,
                             BindingResult bindingResult,
                             @RequestParam(defaultValue = "/") String redirectURL,
                             HttpServletRequest servletRequest) {
        if (bindingResult.hasErrors()) {
            return "user/login";
        }
        boolean result = userLoginService.login(request.getToken());
        if (!result) {
            bindingResult.addError(new FieldError("loginRequest", "token", "로그인 실패"));
            return "user/login";
        }

        HttpSession session = servletRequest.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER.name(), request.getToken());

        if (redirectURL.equals("/")) {
            return "redirect:/targets";
        }
        else {
            return "redirect:" + redirectURL;
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}

