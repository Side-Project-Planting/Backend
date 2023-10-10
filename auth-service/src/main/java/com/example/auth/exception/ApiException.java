package com.example.auth.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApiException(ErrorCode error) {
        super(error.getMessage());
        this.errorCode = error;
    }
}
