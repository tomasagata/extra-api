package org.mojodojocasahouse.extra.controller;

import jakarta.validation.Valid;
import org.mojodojocasahouse.extra.dto.UserAuthenticationRequest;
import org.mojodojocasahouse.extra.dto.UserAuthenticationResponse;
import org.mojodojocasahouse.extra.dto.UserRegistrationResponse;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.mojodojocasahouse.extra.dto.UserRegistrationRequest;

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
            @Valid @RequestBody UserAuthenticationRequest userAuthenticationRequest)
    {
        UserAuthenticationResponse response = userService.authenticateUser(userAuthenticationRequest);
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

}
