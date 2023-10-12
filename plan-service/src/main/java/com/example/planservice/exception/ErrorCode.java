package com.example.planservice.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "플랜이 존재하지 않습니다"),
    MEMBER_NOT_FOUND_IN_PLAN(HttpStatus.NOT_FOUND, "해당 플랜에 소속되지 않은 멤버입니다"),
    TAB_SIZE_LIMIT(HttpStatus.BAD_REQUEST, "하나의 플랜에 탭은 5개까지만 달 수 있습니다"),
    TAB_NAME_DUPLICATE(HttpStatus.BAD_REQUEST, "탭 이름이 중복되었습니다");

    private final HttpStatus status;
    private String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
