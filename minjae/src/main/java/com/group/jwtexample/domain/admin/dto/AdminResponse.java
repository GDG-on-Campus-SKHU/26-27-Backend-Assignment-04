package com.group.jwtexample.domain.admin.dto;

import com.group.jwtexample.domain.user.entity.User;

public record AdminResponse(
        Long id,
        String email,
        Boolean isAdmin
) {

    public static AdminResponse from(User user) {
        return new AdminResponse(user.getId(), user.getEmail(), user.getIsAdmin());
    }
}
