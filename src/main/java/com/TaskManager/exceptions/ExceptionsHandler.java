package com.TaskManager.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        // Add field-specific errors
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status","Bad Request");
        errors.put("error", fieldErrors);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<?> handleDuplicateExceptions(DuplicateKeyException ex, WebRequest request){
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status","Bad Request");
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFoundExceptions(NoSuchElementException ex, WebRequest request){
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status","Bad Request");
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleNotFoundExceptions(MethodArgumentTypeMismatchException ex, WebRequest request){
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status","Bad Request");
        errors.put("error", "Input is invalid");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleValidationExceptions(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> fieldsErrors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            fieldsErrors.put(fieldName, errorMessage);
        }
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status","Bad Request");
        errors.put("error", fieldsErrors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


}


