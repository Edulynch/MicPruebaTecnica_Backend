package com.blautech.pruebaTecnica.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Manejo específico para AccessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetails> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        logger.error("Access denied for request {}: {}", request.getRequestURI(), ex.getMessage());
        ProblemDetails problem = new ProblemDetails(
                "Forbidden",
                HttpStatus.FORBIDDEN.value(),
                "Acceso denegado: se requiere rol ADMIN para acceder a este endpoint",
                request.getRequestURI(),
                "ACCESS_DENIED"
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .header("Content-Type", "application/problem+json")
                .body(problem);
    }

    // Manejo de validación de @Valid (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetails> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.error("Validation error on request {}: {}", request.getRequestURI(), ex.getMessage());
        StringBuilder sb = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            sb.append(error.getDefaultMessage()).append("; ");
        });
        String combinedErrors = sb.toString().trim();
        if (combinedErrors.endsWith(";")) {
            combinedErrors = combinedErrors.substring(0, combinedErrors.length() - 1).trim();
        }
        ProblemDetails problem = new ProblemDetails(
                "Bad Request",
                HttpStatus.BAD_REQUEST.value(),
                combinedErrors,
                request.getRequestURI(),
                "VALIDATION_ERROR"
        );
        return ResponseEntity
                .badRequest()
                .header("Content-Type", "application/problem+json")
                .body(problem);
    }

    // Manejo de ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetails> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        logger.error("ResponseStatusException on request {}: {}", request.getRequestURI(), ex.getMessage());
        HttpStatus httpStatus = HttpStatus.resolve(ex.getStatusCode().value());
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        ProblemDetails problem = new ProblemDetails(
                httpStatus.getReasonPhrase(),
                httpStatus.value(),
                ex.getReason(),
                request.getRequestURI(),
                "RESPONSE_STATUS_ERROR"
        );
        return ResponseEntity
                .status(httpStatus)
                .header("Content-Type", "application/problem+json")
                .body(problem);
    }

    // Manejo general de excepciones
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleGeneralException(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception on request {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetails problem = new ProblemDetails(
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getRequestURI(),
                "INTERNAL_ERROR"
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", "application/problem+json")
                .body(problem);
    }
}
