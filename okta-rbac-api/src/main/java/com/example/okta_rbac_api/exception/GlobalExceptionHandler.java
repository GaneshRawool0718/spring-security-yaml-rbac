package com.example.okta_rbac_api.exception;

import com.okta.sdk.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final Logger ACCESS_LOG = LoggerFactory.getLogger("ACCESS_DENIED");

    /**
     * Handle Access Denied (403 Forbidden) - when user is authenticated but lacks
     * required permissions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth != null ? auth.getName() : "anonymous";
        String authorities = auth != null ? auth.getAuthorities().toString() : "[]";

        ACCESS_LOG.warn("ACCESS_DENIED user={} authorities={} message={}",
                user, authorities, ex.getMessage());

        return buildError(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                "You do not have permission to access this resource");
    }

    /**
     * Handle Authentication failures (401 Unauthorized) - when user is not
     * authenticated.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        ACCESS_LOG.warn("AUTHENTICATION_FAILED message={}", ex.getMessage());

        return buildError(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED",
                "Authentication is required to access this resource");
    }

    @ExceptionHandler(ApplicationUserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleApplicationUserNotFound(ApplicationUserNotFoundException ex) {
        log.error("Application user not found: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "APPLICATION_USER_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(OktaOperationException.class)
    public ResponseEntity<Map<String, String>> handleOktaOperation(OktaOperationException ex) {
        log.error("Okta operation failed", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "OKTA_OPERATION_FAILED",
                "Failed to process request");
    }

    @ExceptionHandler(ResourceException.class)
    public ResponseEntity<Map<String, String>> handleResourceException(ResourceException ex) {
        log.error("Okta resource error: {}", ex.getMessage());
        return buildError(mapStatus(ex.getStatus()), "OKTA_RESOURCE_ERROR", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(
            org.springframework.web.HttpRequestMethodNotSupportedException ex) {
        log.warn("Method not supported: {}", ex.getMessage());
        return buildError(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_SUPPORTED",
                "HTTP method " + ex.getMethod() + " is not supported for this endpoint");
    }

    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleUnsupportedMediaType(
            org.springframework.web.HttpMediaTypeNotSupportedException ex) {
        log.warn("Unsupported media type: {}", ex.getContentType());
        return buildError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE",
                "Content-Type '" + ex.getContentType() + "' is not supported. Expected 'application/json'");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred");
    }

    private ResponseEntity<Map<String, String>> buildError(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(Map.of("error", error, "message", message));
    }

    private HttpStatus mapStatus(int status) {
        return switch (status) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 403 -> HttpStatus.FORBIDDEN;
            case 404 -> HttpStatus.NOT_FOUND;
            case 429 -> HttpStatus.TOO_MANY_REQUESTS;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
