package com.example.seven.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeDTO {
    private String currentPassword; // 현재 비밀번호
    private String newPassword;     // 새 비밀번호
}