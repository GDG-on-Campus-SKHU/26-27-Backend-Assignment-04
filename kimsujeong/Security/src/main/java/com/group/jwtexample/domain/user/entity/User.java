package com.group.jwtexample.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // enum 이름 값을 DB에 저장
    @Column(nullable = false)
    private Role role; // 사용자 역할: USER, ADMIN

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = Role.USER; // 일반 회원가입으로 생성되는 계정의 역할 USER 강제
    }

    public static User createAdmin(String email, String encodedPassword) {
        User user = new User(email, encodedPassword);
        user.role = Role.ADMIN;
        return user;
    }
}
