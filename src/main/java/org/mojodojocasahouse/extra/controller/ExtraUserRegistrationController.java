package org.mojodojocasahouse.extra.controller;

import jakarta.validation.Valid;
import org.mojodojocasahouse.extra.dto.ExtraUserRegistrationResponseDto;
import org.mojodojocasahouse.extra.service.ExtraUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.mojodojocasahouse.extra.dto.ExtraUserRegistrationDto;

@Controller
public class ExtraUserRegistrationController {

    private final ExtraUserService userService;

    @Autowired
    public ExtraUserRegistrationController(ExtraUserService userService) {
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
    public ResponseEntity<ExtraUserRegistrationResponseDto> registerUserAccount(@Valid @RequestBody ExtraUserRegistrationDto extraUserRegistrationDto) {
        return new ResponseEntity<>(
                userService.registrarUsuario(extraUserRegistrationDto),
                HttpStatus.CREATED
        );
    }
}
