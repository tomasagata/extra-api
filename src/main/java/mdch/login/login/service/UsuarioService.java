package mdch.login.login.service;

import jakarta.validation.Valid;
import mdch.login.login.dto.UsuarioRegistroDto;
import mdch.login.login.dto.UsuarioRegistroResponseDto;
import mdch.login.login.model.impl.Usuario;


public interface UsuarioService {

    UsuarioRegistroResponseDto registrarUsuario(UsuarioRegistroDto registroDTO);
}
