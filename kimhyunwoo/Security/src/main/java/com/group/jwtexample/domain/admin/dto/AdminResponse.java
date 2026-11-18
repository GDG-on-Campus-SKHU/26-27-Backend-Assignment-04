package com.group.jwtexample.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.group.jwtexample.domain.user.entity.Role;
import com.group.jwtexample.domain.user.entity.User;

public record AdminResponse(
        Long id,
        String email,
        @JsonProperty("isAdmin") boolean isAdmin
) {

    public static AdminResponse from(User user) {
        return new AdminResponse(user.getId(), user.getEmail(), user.isAdmin());
    }
}
