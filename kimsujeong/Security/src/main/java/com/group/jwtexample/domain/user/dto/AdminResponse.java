package com.group.jwtexample.domain.user.dto;

import com.group.jwtexample.domain.user.entity.Role;
import com.group.jwtexample.domain.user.entity.User;

public record AdminResponse(
        Long id,
        String email,
        Boolean isAdmin
) {
    public static AdminResponse from(User user) { return new AdminResponse(user.getId(), user.getEmail(), user.getRole() == Role.ADMIN); }
}
