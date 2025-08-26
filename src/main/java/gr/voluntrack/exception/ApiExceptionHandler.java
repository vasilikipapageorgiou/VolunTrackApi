package gr.voluntrack.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private Map<String, Object> body(HttpStatus status, String error, String message, String path) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", error,
                "message", message,
                "path", path
        );
    }

    // 400 - validation errors from @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.debug("Validation error: {}", ex.getMessage());

        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "rejectedValue", Objects.toString(fe.getRejectedValue(), ""),
                        "message", fe.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("timestamp", LocalDateTime.now());
        resp.put("status", HttpStatus.BAD_REQUEST.value());
        resp.put("error", "ValidationError");
        resp.put("message", "Validation failed for one or more fields");
        resp.put("path", request.getRequestURI());
        resp.put("fieldErrors", fieldErrors);

        return new ResponseEntity<>(resp, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // 404 / 400 via ResponseStatusException (useful when you throw new ResponseStatusException)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        log.debug("ResponseStatusException: {}", ex.getMessage());
        Map<String, Object> resp = body(HttpStatus.NOT_FOUND, "Not found", ex.getReason() != null ? ex.getReason() : ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
    }

    // 401 - authentication failures
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        log.debug("Authentication failed: {}", ex.getMessage());
        Map<String, Object> resp = body(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(resp, HttpStatus.UNAUTHORIZED);
    }

    // 403 - access denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.debug("Access denied: {}", ex.getMessage());
        Map<String, Object> resp = body(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);
    }

    // 409 - data integrity / unique constraints
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        Map<String, Object> resp = body(HttpStatus.CONFLICT, "Conflict", "Database error: " + ex.getMostSpecificCause().getMessage(), request.getRequestURI());
        return new ResponseEntity<>(resp, HttpStatus.CONFLICT);
    }

    // 422 - illegal state / business logic errors
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Object> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        log.debug("Bad request: {}", ex.getMessage());
        Map<String, Object> resp = body(HttpStatus.UNPROCESSABLE_ENTITY, "UnprocessableEntity", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(resp, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // fallback - 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred", ex);
        Map<String, Object> resp = body(HttpStatus.INTERNAL_SERVER_ERROR, "InternalServerError", "An unexpected error occurred", request.getRequestURI());
        // optionally attach ex.getMessage() in dev, but avoid leaking details in production
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
