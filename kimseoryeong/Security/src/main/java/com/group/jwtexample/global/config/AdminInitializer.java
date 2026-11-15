package com.group.jwtexample.global.config;

import com.group.jwtexample.domain.user.entity.Role;
import com.group.jwtexample.domain.user.entity.User;
import com.group.jwtexample.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User(adminEmail, passwordEncoder.encode(adminPassword), Role.ADMIN);
            userRepository.save(admin);
        }
    }
}
