package com.group.jwtexample.domain.admin.dto;

import com.group.jwtexample.domain.user.entity.User;
import com.group.jwtexample.domain.user.entity.Role;

public record AdminResponse (
    Long id,
    String email,
    boolean isAdmin
) {
   public static AdminResponse from(User user) {
       return new AdminResponse(
                user.getId(),
                user.getEmail(),
               user.getRole() == Role.ADMIN
       );
   }
}
