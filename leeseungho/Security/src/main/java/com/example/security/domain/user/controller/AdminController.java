package com.example.security.domain.user.controller;

import com.example.security.domain.user.dto.AdminMeResponse;
import com.example.security.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/me")
    public AdminMeResponse getAdminInfo(@AuthenticationPrincipal Long userId) {
        return userService.getAdminInfo(userId);
    }
}
