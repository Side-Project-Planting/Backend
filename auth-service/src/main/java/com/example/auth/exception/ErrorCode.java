package com.example.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    OAUTH_PROVIDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당되는 OAuth 타입은 지원하지 않습니다."),
    ACCESS_TOKEN_FETCH_FAIL(HttpStatus.BAD_REQUEST, "Access Token을 받아오는 데 실패했습니다. 입력값을 확인하세요"),
    USER_INFO_FETCH_FAIL(HttpStatus.BAD_REQUEST, "사용자의 정보를 받아오는 데 실패했습니다."),
    AUTH_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "해당되는 인증 정보를 찾을 수 없습니다"),
    TOKEN_ID_INVALID(HttpStatus.BAD_REQUEST, "토큰의 ID값이 잘못되었습니다."),
    TOKEN_TIMEOVER(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다"),
    REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "사용자의 refresh token이 일치하지 않습니다"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에러입니다. 관리자에게 문의하세요"),
    EXTERNAL_AUTH_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "현재 외부 Auth 서버가 정상 동작하지 않습니다"),
    TOKEN_UNAUTHORIZED(HttpStatus.FORBIDDEN, "Authorized Token이 일치하지 않습니다"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당되는 멤버를 찾을 수 없습니다");

    private final HttpStatus status;
    private String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
