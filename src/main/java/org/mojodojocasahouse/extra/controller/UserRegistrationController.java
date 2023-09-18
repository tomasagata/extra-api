package org.mojodojocasahouse.extra.controller;

import jakarta.validation.Valid;
import org.mojodojocasahouse.extra.dto.UserRegistrationResponse;
import org.mojodojocasahouse.extra.exception.MismatchingPasswordsException;
import org.mojodojocasahouse.extra.service.ExtraUserService;
import org.mojodojocasahouse.extra.service.impl.ExtraUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.mojodojocasahouse.extra.dto.UserRegistrationRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserRegistrationController {

    private final ExtraUserServiceImpl userService;

    @Autowired
    public UserRegistrationController(ExtraUserServiceImpl userService) {
        this.userService = userService;
    }

//    @ModelAttribute("usuario")
//    public UsuarioRegistroDTO usuarioRegistroDTO() {
//        return new UsuarioRegistroDTO();
//    }

//    @GetMapping("/registro")
//    public String mostrarFormularioRegistro() {
//        return "registroTemplate";
//    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserRegistrationResponse> registerUserAccount(
            @Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {

            UserRegistrationResponse response = userService.registerUser(userRegistrationRequest);
            return new ResponseEntity<>(
                    response,
                    HttpStatus.CREATED
            );
    }
}
