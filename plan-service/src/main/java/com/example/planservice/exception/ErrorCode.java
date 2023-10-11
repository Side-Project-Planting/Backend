package com.example.planservice.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    TAB_SIZE_LIMIT(HttpStatus.BAD_REQUEST, "하나의 Plan에 Tab은 5개까지만 달 수 있습니다");

    private final HttpStatus status;
    private String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
