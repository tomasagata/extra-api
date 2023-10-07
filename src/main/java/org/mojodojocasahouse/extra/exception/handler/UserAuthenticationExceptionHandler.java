package org.mojodojocasahouse.extra.exception.handler;

import org.mojodojocasahouse.extra.exception.*;
import org.mojodojocasahouse.extra.dto.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.www.NonceExpiredException;
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
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Failed to read request",
                "Malformed Request"
        );
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

    @ExceptionHandler({
            InsufficientAuthenticationException.class,
            ProviderNotFoundException.class,
            AuthenticationCredentialsNotFoundException.class,
            BadCredentialsException.class,
            NonceExpiredException.class,
            PreAuthenticatedCredentialsNotFoundException.class,
            SessionAuthenticationException.class,
            UsernameNotFoundException.class
    })
    protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex,
                                                                   WebRequest request){
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Authentication Error",
                ex.getMessage()
        );
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
    }

    @ExceptionHandler({
            AccountExpiredException.class,
            CredentialsExpiredException.class,
            DisabledException.class,
            LockedException.class
    })
    protected ResponseEntity<Object> handleAccountStatusException(AccountStatusException ex, WebRequest request){
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Account Status Error",
                ex.getMessage()
        );
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
    }

    @ExceptionHandler({
            CookieTheftException.class, // Why not?
            InvalidCookieException.class
    })
    protected ResponseEntity<Object> handleRememberMeAuthenticationException(RememberMeAuthenticationException ex,
                                                                             WebRequest request){
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "RememberMe Authentication Error",
                ex.getMessage()
        );
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
    }

    @ExceptionHandler({
            AuthenticationServiceException.class,
            InternalAuthenticationServiceException.class
    })
    protected ResponseEntity<Object> handleInternalAuthenticationServiceException(
            InternalAuthenticationServiceException ex, WebRequest request){
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Authentication Service Error",
                ex.getMessage()
        );
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
    }

}
