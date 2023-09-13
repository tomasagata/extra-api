package org.mojodojocasahouse.extra.service.impl;

import jakarta.validation.ConstraintViolationException;
import org.mojodojocasahouse.extra.dto.UsuarioRegistroDto;
import org.mojodojocasahouse.extra.dto.UsuarioRegistroResponseDto;
import org.mojodojocasahouse.extra.model.impl.Usuario;
import org.mojodojocasahouse.extra.repository.UsuarioRepository;
import org.mojodojocasahouse.extra.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioRegistroResponseDto registrarUsuario( UsuarioRegistroDto registroDTO)
                                                            throws ConstraintViolationException {
        Usuario usuario = new Usuario(registroDTO.getNombre(), registroDTO.getApellido(), registroDTO.getEmail(), registroDTO.getPassword());
        Usuario registeredUser = usuarioRepository.save(usuario);
        // So something with registered user
        return new UsuarioRegistroResponseDto("Usuario creado satisfactoriamente");
    }
}
