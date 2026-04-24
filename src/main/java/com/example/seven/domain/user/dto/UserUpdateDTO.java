package com.example.seven.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private String email;
    private String phoneNumber;
    private String address;
}
