
package com.group.security.domain.admin.controller;

import com.group.security.domain.admin.dto.AdminResponse;
import com.group.security.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/me")
    public AdminResponse getMyInfo(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        return userService.getAdminInfo(userId);
    }
}
