package org.mojodojocasahouse.extra.service;

import org.mojodojocasahouse.extra.dto.UsuarioRegistroDto;
import org.mojodojocasahouse.extra.dto.UsuarioRegistroResponseDto;


public interface UsuarioService {

    UsuarioRegistroResponseDto registrarUsuario(UsuarioRegistroDto registroDTO);
}
