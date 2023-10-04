package org.mojodojocasahouse.extra.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class AuthenticationController {

    private final AuthenticationService userService;

    @Autowired
    public AuthenticationController(AuthenticationService userService) {
        this.userService = userService;
    }


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

    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> loginUser(
            @Valid @RequestBody UserAuthenticationRequest userAuthenticationRequest,
            HttpServletResponse servletResponse)
    {
        // Authenticate new user
        Pair<ApiResponse, Cookie> responseCookiePair = userService.authenticateUser(userAuthenticationRequest);

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
    public ResponseEntity<ApiResponse> protectedResource(@CookieValue("JSESSIONID") UUID cookie)
    {
        userService.validateAuthentication(cookie);
        return new ResponseEntity<>(
                new ApiResponse("Authenticated and authorized!"),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/logout", produces = "application/json")
    public ResponseEntity<ApiResponse> logoutUser(@CookieValue("JSESSIONID") UUID sessionId, HttpServletResponse servletResponse)
    {
        userService.validateAuthentication(sessionId);
        userService.revokeCredentials(sessionId);

        // Create a new cookie with zero life to delete the existing cookie in the client.
        Cookie zeroTtlCookie = new Cookie("JSESSIONID", null);
        zeroTtlCookie.setMaxAge(0);

        // Append Set-Cookie header to response
        servletResponse.addCookie(
                zeroTtlCookie
        );

        return new ResponseEntity<>(
                new ApiResponse("User logout successful"),
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/auth/password/change", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody UserChangePasswordRequest userChangePasswordRequest,@CookieValue("JSESSIONID") UUID cookie){
        userService.validateAuthentication(cookie);
        ExtraUser user = userService.getUserBySessionToken(cookie);
        ApiResponse response = userService.changePassword(user,userChangePasswordRequest);
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

}
