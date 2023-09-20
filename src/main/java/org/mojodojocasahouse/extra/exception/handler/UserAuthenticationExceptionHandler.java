package org.mojodojocasahouse.extra.exception.handler;

import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.exception.InvalidCredentialsException;
import org.mojodojocasahouse.extra.exception.handler.helper.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class UserAuthenticationExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Data validation error", errors);
        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Failed to read request", "Malformed Request");
        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }

    @ExceptionHandler(ExistingUserEmailException.class)
    protected ResponseEntity<Object> handleExistingUserEmail(ExistingUserEmailException ex, WebRequest request){
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT,
                "User registration conflict",
                ex.getMessage()
        );
        return  handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    protected ResponseEntity<Object> handleInvalidCredentials(InvalidCredentialsException ex, WebRequest request){
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "User Authentication Error",
                ex.getMessage()
        );
        return  handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
    }

}
