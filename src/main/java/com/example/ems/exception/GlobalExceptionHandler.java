package com.example.ems.exception;

import com.example.ems.response.Response;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;
import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidRequestException.class)
    public Mono<ResponseEntity<Response>> handleInvalid(InvalidRequestException ex) {
        return Mono.just(ResponseEntity
                .badRequest()
                .body(Response.error(ex.getMessage(), 400, null)));
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public Mono<ResponseEntity<Response>> handleEmployeeNotFound(EmployeeNotFoundException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.error(ex.getMessage(), 404, null)));
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public Mono<ResponseEntity<Response>> handleDepartmentNotFound(DepartmentNotFoundException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.error(ex.getMessage(), 404, null)));
    }

    @ExceptionHandler(DataConflictException.class)
    public Mono<ResponseEntity<Response>> handleDataConflict(DataConflictException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Response.error(ex.getMessage(), 409, null)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<Response>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));
        return Mono.just(ResponseEntity
                .badRequest()
                .body(Response.error("Validation failed", 400, errors)));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<Response>> handleConstraintViolation(ConstraintViolationException ex) {
        return Mono.just(ResponseEntity
                .badRequest()
                .body(Response.error("Validation failed", 400, ex.getMessage())));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<Response>> handleAccessDenied(AccessDeniedException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Response.error("Forbidden: You don't have permission to access this resource.", 403, null)));
    }

    @ExceptionHandler(AuthenticationException.class)
    public Mono<ResponseEntity<Response>> handleAuthentication(AuthenticationException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Response.error("Unauthorized: Please login to access this resource.", 401, null)));
    }
}
