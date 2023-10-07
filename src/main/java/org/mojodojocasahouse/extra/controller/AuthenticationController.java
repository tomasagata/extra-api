package org.mojodojocasahouse.extra.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService userService;

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> registerUserAccount(
            @Valid @RequestBody UserRegistrationRequest userRegistrationRequest)
    {
            ApiResponse response = userService.registerUser(userRegistrationRequest);
            return new ResponseEntity<>(
                    response,
                    HttpStatus.CREATED
            );
    }

    @PostMapping(path = "/login", produces = "application/json")
    public ResponseEntity<ApiResponse> login() {
        return new ResponseEntity<>(
                new ApiResponse("Login successful"),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/protected", produces = "application/json")
    public ResponseEntity<ApiResponse> protectedResource()
    {
        return new ResponseEntity<>(
                new ApiResponse("Authenticated and authorized!"),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/fullyProtected", produces = "application/json")
    public ResponseEntity<ApiResponse> fullyProtectedResource()
    {
        return new ResponseEntity<>(
                new ApiResponse("Fully authenticated and authorized!"),
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/auth/password/change", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody UserChangePasswordRequest userChangePasswordRequest,
                                                      Principal principal){
        ExtraUser user = userService.getUserByPrincipal(principal);
        ApiResponse response = userService.changePassword(user,userChangePasswordRequest);
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

}
