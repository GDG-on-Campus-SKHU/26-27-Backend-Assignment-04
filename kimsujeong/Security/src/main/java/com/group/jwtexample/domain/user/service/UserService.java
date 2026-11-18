package com.group.jwtexample.domain.user.service;

import com.group.jwtexample.domain.user.dto.AdminResponse;
import com.group.jwtexample.domain.user.dto.UserResponse;
import com.group.jwtexample.domain.user.entity.Role;
import com.group.jwtexample.domain.user.entity.User;
import com.group.jwtexample.domain.user.repository.UserRepository;
import com.group.jwtexample.global.exception.ErrorCode;
import com.group.jwtexample.global.exception.ForbiddenException;
import com.group.jwtexample.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.from(user);
    }

    public AdminResponse getAdminInfo(Long userId) { // admin 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != Role.ADMIN) {
            throw new ForbiddenException(ErrorCode.ADMIN_FORBIDDEN); // admin이 아니면 403 예외
        }

        return AdminResponse.from(user);
    }
}
