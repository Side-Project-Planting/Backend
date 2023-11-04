package com.example.planservice.exception;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception) {
        return ResponseEntity.status(exception.getErrorCode().getStatus())
            .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindingResultException(BindException exception) {
        List<ObjectError> allErrors = exception.getBindingResult().getAllErrors();
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(allErrors.get(0).getDefaultMessage()));
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> handleDatabaseConflict() {
        ErrorCode errorCode = ErrorCode.REQUEST_CONFLICT;
        return ResponseEntity.status(errorCode.getStatus())
            .body(new ErrorResponse(errorCode.getMessage()));
    }

}
