package com.group.jwtexample.domain.auth.controller;

import com.group.jwtexample.domain.auth.dto.LoginRequest;
import com.group.jwtexample.domain.auth.dto.RefreshRequest;
import com.group.jwtexample.domain.auth.dto.SignupRequest;
import com.group.jwtexample.domain.auth.dto.TokenResponse;
import com.group.jwtexample.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        authService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
