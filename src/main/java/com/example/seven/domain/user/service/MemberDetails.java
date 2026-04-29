package com.example.seven.domain.user.service;

import com.example.seven.domain.user.entity.MemberEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class MemberDetails implements UserDetails {

    private final MemberEntity entity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 🌟 Enum 이름(USER) 앞에 "ROLE_"을 붙여서 시큐리티가 인식할 수 있게 합니다.
        return Collections.singletonList(new SimpleGrantedAuthority(entity.getRole().name()));
    }

    @Override
    public String getPassword() {
        return entity.getPassword();
    }

    @Override
    public String getUsername() {
        return entity.getUsername();
    }

    public String getEmail() {
        return entity.getEmail();
    }
    public String getPhoneNumber() {
        return entity.getPhoneNumber();
    }
    public String getAddress() {
        return entity.getAddress();
    }
    public String getRole() {
        return entity.getRole().getDescription();
    }


    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
