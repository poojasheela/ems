package com.example.ems.exception;

import com.example.ems.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import reactor.core.publisher.Mono;

import jakarta.validation.ConstraintViolationException;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleInvalidRequestException() {
        InvalidRequestException ex = new InvalidRequestException("Invalid request");
        Mono<ResponseEntity<Response>> result = handler.handleInvalid(ex);

        ResponseEntity<Response> response = result.block();
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid request", response.getBody().getMessage());
    }

    @Test
    void testHandleEmployeeNotFoundException() {
        EmployeeNotFoundException ex = new EmployeeNotFoundException("Employee not found");
        ResponseEntity<Response> response = handler.handleEmployeeNotFound(ex).block();

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Employee not found", response.getBody().getMessage());
    }

    @Test
    void testHandleDepartmentNotFoundException() {
        DepartmentNotFoundException ex = new DepartmentNotFoundException("Department not found");
        ResponseEntity<Response> response = handler.handleDepartmentNotFound(ex).block();

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Department not found", response.getBody().getMessage());
    }

    @Test
    void testHandleDataConflictException() {
        DataConflictException ex = new DataConflictException("Conflict error");
        ResponseEntity<Response> response = handler.handleDataConflict(ex).block();

        assertNotNull(response);
        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Conflict error", response.getBody().getMessage());
    }

    @Test
    void testHandleValidationErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "email", "Invalid email");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<Response> response = handler.handleValidationErrors(ex).block();

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertTrue(response.getBody().getData().toString().contains("Invalid email"));
    }

    @Test
    void testHandleConstraintViolationException() {
        ConstraintViolationException ex = new ConstraintViolationException("constraint violated", new HashSet<>());
        ResponseEntity<Response> response = handler.handleConstraintViolation(ex).block();

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertTrue(response.getBody().getData().toString().contains("constraint violated"));
    }

    @Test
    void testHandleAccessDenied() {
        // Arrange
        AccessDeniedException ex = new AccessDeniedException("Forbidden");

        // Act
        ResponseEntity<Response> response = handler.handleAccessDenied(ex).block();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Forbidden: You don't have permission to access this resource.",
                response.getBody().getMessage());
        assertEquals(403, response.getBody().getStatus());
    }

    @Test
    void testHandleAuthenticationException() {
        AuthenticationException ex = mock(AuthenticationException.class);
        when(ex.getMessage()).thenReturn("Unauthorized access");
        ResponseEntity<Response> response = handler.handleAuthentication(ex).block();

        assertNotNull(response);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized: Please login to access this resource.", response.getBody().getMessage());
    }
}
