package com.example.security.domain.user.service;

import com.example.security.domain.user.dto.AdminMeResponse;
import com.example.security.domain.user.dto.UserResponse;
import com.example.security.domain.user.entity.User;
import com.example.security.domain.user.repository.UserRepository;
import com.example.security.global.exception.ErrorCode;
import com.example.security.global.exception.NotFoundException;
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

    public AdminMeResponse getAdminInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return AdminMeResponse.from(user);
    }
}
