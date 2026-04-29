package com.example.seven.domain.user.entity;


public enum Role {
    // 스프링 시큐리티 규격에 맞춘 권한명과, 화면에 보여줄 한글 이름
    ROLE_USER("일반회원(점주)"),
    ROLE_WAREHOUSE_MANAGER("창고관리자"),
    ROLE_GENERAL_MANAGER("본사관리자");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}