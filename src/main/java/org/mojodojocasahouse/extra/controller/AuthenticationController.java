package org.mojodojocasahouse.extra.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.mojodojocasahouse.extra.dto.UserAuthenticationRequest;
import org.mojodojocasahouse.extra.dto.UserAuthenticationResponse;
import org.mojodojocasahouse.extra.dto.UserRegistrationResponse;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.mojodojocasahouse.extra.dto.UserRegistrationRequest;

import java.util.UUID;

@RestController
public class AuthenticationController {

    private final AuthenticationService userService;

    @Autowired
    public AuthenticationController(AuthenticationService userService) {
        this.userService = userService;
    }


    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserRegistrationResponse> registerUserAccount(
            @Valid @RequestBody UserRegistrationRequest userRegistrationRequest)
    {
            UserRegistrationResponse response = userService.registerUser(userRegistrationRequest);
            return new ResponseEntity<>(
                    response,
                    HttpStatus.CREATED
            );
    }

    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserAuthenticationResponse> loginEmployee(
            @Valid @RequestBody UserAuthenticationRequest userAuthenticationRequest,
            HttpServletResponse servletResponse)
    {
        // Authenticate new user
        Pair<UserAuthenticationResponse, Cookie> responseCookiePair = userService.authenticateUser(userAuthenticationRequest);

        // Add new JSESSIONID cookie to prevent session-fixation
        servletResponse.addCookie(
                responseCookiePair.getSecond()
        );

        // Return response
        return new ResponseEntity<>(
                responseCookiePair.getFirst(),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/protected", produces = "application/json")
    public ResponseEntity<Object> protectedResource(@CookieValue("JSESSIONID") UUID cookie)
    {
        userService.validateAuthentication(cookie);
//        userService.validateAuthorization(cookie);
        return new ResponseEntity<>(
                "Authenticated and authorized!",
                HttpStatus.OK
        );
    }

}
