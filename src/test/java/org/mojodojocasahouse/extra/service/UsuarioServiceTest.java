package org.mojodojocasahouse.extra.service;

import org.mojodojocasahouse.extra.dto.UsuarioRegistroDto;
import org.mojodojocasahouse.extra.dto.UsuarioRegistroResponseDto;
import org.mojodojocasahouse.extra.model.impl.Usuario;
import org.mojodojocasahouse.extra.repository.UsuarioRepository;
import org.mojodojocasahouse.extra.service.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repo;

    @InjectMocks
    private UsuarioServiceImpl serv;

    @Test
    public void testPassingAUserRegistrationRequestReturnsASuccessfulResponse() {
        // Setup - data
        Usuario mj = new Usuario(
                "Michael",
                "Jordan",
                "mj@me.com",
                "somepassword"
        );
        UsuarioRegistroDto mjDto = new UsuarioRegistroDto(
                "Michael",
                "Jordan",
                "mj@me.com",
                "somepassword");
        UsuarioRegistroResponseDto successfulResponse= new UsuarioRegistroResponseDto(
                "Usuario creado satisfactoriamente"
        );

        // Setup – expectations
        given(repo.save(any(Usuario.class))).willReturn(mj);

        // exercise
        UsuarioRegistroResponseDto response = serv.registrarUsuario(mjDto);

        // verify
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response).isEqualTo(successfulResponse);
    }

}
