package com.example.auth.exception;

import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public class MicroserviceException extends RuntimeException {
    private final HttpStatusCode code;

    public MicroserviceException(HttpStatusCode code, String message) {
        super(message);
        this.code = code;
    }
}
