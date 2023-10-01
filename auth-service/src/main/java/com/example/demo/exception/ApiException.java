package com.example.demo.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApiException(ErrorCode e) {
        super(e.getMessage());
        this.errorCode = e;
    }
}