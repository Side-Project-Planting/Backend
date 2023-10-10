package com.example.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    OAUTH_PROVIDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당되는 OAuth 타입은 지원하지 않습니다."),
    ACCESS_TOKEN_FETCH_FAIL(HttpStatus.BAD_REQUEST, "Access Token을 받아오는 데 실패했습니다."),
    USER_INFO_FETCH_FAIL(HttpStatus.BAD_REQUEST, "사용자의 정보를 받아오는 데 실패했습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    TOKEN_ID_INVALID(HttpStatus.BAD_REQUEST, "토큰의 ID값이 잘못되었습니다."),
    TOKEN_TIMEOVER(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다"),
    REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "사용자의 refresh token이 일치하지 않습니다");

    private final HttpStatus status;
    private String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
