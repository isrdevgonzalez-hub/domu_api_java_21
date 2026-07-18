package com.api.domu.domu.shared;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException exception, HttpServletRequest request) {
        HttpStatus status = resolveStatus(exception.getCode());
        return ResponseEntity.status(status).body(buildResponse(exception.getCode(), exception.getMessage(),
                status.value(), request.getRequestURI(), Map.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception,
                                                                   HttpServletRequest request) {
        Map<String, String> validations = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            validations.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(buildResponse("VALIDATION_ERROR", "Request validation failed",
                HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), validations));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception,
                                                                   HttpServletRequest request) {
        return ResponseEntity.badRequest().body(buildResponse("VALIDATION_ERROR", exception.getMessage(),
                HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), Map.of()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException exception,
                                                             HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildResponse("DATA_INTEGRITY_ERROR",
                "The operation violates a database constraint", HttpStatus.CONFLICT.value(),
                request.getRequestURI(), Map.of()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildResponse("INTERNAL_ERROR",
                "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI(), Map.of()));
    }

    private ErrorResponse buildResponse(String code, String message, int status, String path,
                                        Map<String, String> validationErrors) {
        return new ErrorResponse(code, message, status, path, LocalDateTime.now(), validationErrors);
    }

    private HttpStatus resolveStatus(String code) {
        return switch (code) {
            case "CLIENT_NOT_FOUND", "PERSON_NOT_FOUND", "CONTACT_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "CLIENT_RUT_ALREADY_EXISTS", "PERSON_RUT_ALREADY_EXISTS", "CONTACT_ALREADY_ASSOCIATED" ->
                    HttpStatus.CONFLICT;
            case "CLIENT_INACTIVE", "PERSON_INACTIVE", "CLIENT_CONTACT_INACTIVE",
                    "INVALID_CLIENT_RUT", "INVALID_PERSON_RUT", "CONTACT_EMAIL_REQUIRED",
                    "INVALID_CONTACT_TYPE" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
