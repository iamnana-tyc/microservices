package com.iamnana.microservice.handler;

import com.iamnana.microservice.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleProductPurchaseException(BusinessException exp) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(exp.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorObject> handleMethodNotAllowedException(MethodArgumentNotValidException exp){
        var errors = new HashMap<String, String>();
        exp.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    var fieldName = ((FieldError) error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.put(fieldName,errorMessage);
                });
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ErrorObject(errors));

    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException exp) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(exp.getMessage());
    }

}
