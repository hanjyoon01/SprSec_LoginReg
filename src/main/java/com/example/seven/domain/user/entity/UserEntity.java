package com.example.seven.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserEntity {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // username 중복 방지
    @Column(nullable = false, unique = true)
    private String username;
    private String password;
    private String email;

    //UserRole Enum 사용
    //String 그대로 사용되도록
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
