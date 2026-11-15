package com.group.security.domain.user.controller;

import com.group.security.domain.user.dto.AdminResponse;
import com.group.security.domain.user.service.UserService;
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
    public AdminResponse getAdminMe(@AuthenticationPrincipal Long userId) {
        return userService.getMyAdminInfo(userId);
    }
}
