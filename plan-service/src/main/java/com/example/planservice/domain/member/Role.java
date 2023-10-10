package com.example.planservice.domain.member;

public enum Role {
    ADMIN("관리자"),
    USER("일반 사용자");

    private final String text;

    Role(String text) {
        this.text = text;
    }
}
