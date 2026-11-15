package com.group.jwtexample.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.group.jwtexample.domain.user.entity.User;

public record AdminMeResponse(
        Long id,
        String email,
        @JsonProperty("isAdmin") boolean isAdmin
) {

    public static AdminMeResponse from(User user) {
        return new AdminMeResponse(user.getId(), user.getEmail(), user.isAdmin());
    }
}
