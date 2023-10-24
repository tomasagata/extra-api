package org.mojodojocasahouse.extra.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService userService;

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> registerUserAccount(
            @Valid @RequestBody UserRegistrationRequest userRegistrationRequest)
    {
            log.debug("Registering user \"" + userRegistrationRequest.getEmail() + "\"");

            ApiResponse response = userService.registerUser(userRegistrationRequest);

            return new ResponseEntity<>(
                    response,
                    HttpStatus.CREATED
            );
    }

    @PostMapping(path = "/login", produces = "application/json")
    public ResponseEntity<ApiResponse> login(Principal principal) {
        log.debug("User \"" + principal.getName() + "\" authenticated successfully");

        ExtraUser user = userService.getUserByPrincipal(principal);
        Map<String, String> userResponse = new HashMap<>();
        userResponse.put("firstName", user.getFirstName());
        userResponse.put("lastName", user.getLastName());

        return new ResponseEntity<>(
                new ApiResponse("Login successful", userResponse),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/protected", produces = "application/json")
    public ResponseEntity<ApiResponse> protectedResource(Principal principal)
    {
        log.debug("User \"" + principal.getName() + "\" accessed protected endpoint");

        return new ResponseEntity<>(
                new ApiResponse("Authenticated and authorized!"),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/fullyProtected", produces = "application/json")
    public ResponseEntity<ApiResponse> fullyProtectedResource(Principal principal)
    {
        log.debug("User \"" + principal.getName() + "\" accessed fully protected endpoint");

        return new ResponseEntity<>(
                new ApiResponse("Fully authenticated and authorized!"),
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/auth/password/change", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody UserChangePasswordRequest userChangePasswordRequest,
                                                      Principal principal){
        log.debug("User \"" + principal.getName() + "\" requested a password change");

        ExtraUser user = userService.getUserByPrincipal(principal);
        ApiResponse response = userService.changePassword(user,userChangePasswordRequest);
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/auth/forgotten")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request){
        log.debug("Email \"" + request.getEmail() + "\" initiated a password forgotten flow");

        ApiResponse response = userService.sendPasswordResetEmail(request);
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/auth/forgotten/reset")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody PasswordResetRequest request){
        log.debug("Token \"" + request.getToken() + "\" was used to reset password");

        ApiResponse response = userService.resetPassword(request);
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

}
