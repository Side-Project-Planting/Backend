package com.example.demo.domain;

import lombok.Getter;

public enum OAuthType {
    GOOGLE("google");

    @Getter
    private final String text;

    OAuthType(String text) {
        this.text = text;
    }
}

