package mdch.login.login.service.impl;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import mdch.login.login.dto.UsuarioRegistroDto;
import mdch.login.login.dto.UsuarioRegistroResponseDto;
import mdch.login.login.model.impl.Usuario;
import mdch.login.login.repository.UsuarioRepository;
import mdch.login.login.service.UsuarioService;
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
