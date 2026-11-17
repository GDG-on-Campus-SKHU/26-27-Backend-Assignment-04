package com.example.security.domain.user.dto;

import com.example.security.domain.user.entity.Role;
import com.example.security.domain.user.entity.User;

public record AdminMeResponse(
        Long id,
        String email,
        Role role,
        boolean isAdmin
) {
    public static AdminMeResponse from(User user) {
        return new AdminMeResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getRole() == Role.ADMIN
        );
    }
}
