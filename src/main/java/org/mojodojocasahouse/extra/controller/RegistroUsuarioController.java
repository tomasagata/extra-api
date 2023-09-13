package org.mojodojocasahouse.extra.controller;

import jakarta.validation.Valid;
import org.mojodojocasahouse.extra.dto.UsuarioRegistroResponseDto;
import org.mojodojocasahouse.extra.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.mojodojocasahouse.extra.dto.UsuarioRegistroDto;

@Controller
public class RegistroUsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public RegistroUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

//    @ModelAttribute("usuario")
//    public UsuarioRegistroDTO usuarioRegistroDTO() {
//        return new UsuarioRegistroDTO();
//    }

//    @GetMapping("/registro")
//    public String mostrarFormularioRegistro() {
//        return "registroTemplate";
//    }

    @PostMapping(value = "/registro", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UsuarioRegistroResponseDto> registrarCuentaDeUsuario(@Valid @RequestBody UsuarioRegistroDto usuarioRegistroDTO) {
        return new ResponseEntity<>(
                usuarioService.registrarUsuario(usuarioRegistroDTO),
                HttpStatus.CREATED
        );
    }
}
