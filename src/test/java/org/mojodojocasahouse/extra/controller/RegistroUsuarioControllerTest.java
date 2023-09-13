package org.mojodojocasahouse.extra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mojodojocasahouse.extra.dto.UsuarioRegistroDto;
import org.mojodojocasahouse.extra.dto.UsuarioRegistroResponseDto;
import org.mojodojocasahouse.extra.service.UsuarioService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RegistroUsuarioControllerTest {

    private MockMvc mvc;

    @Mock
    public UsuarioService service;

    @InjectMocks
    public RegistroUsuarioController controller;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testPostingUnregisteredUserShouldReturnSuccessResponse() throws Exception {
        // Setup - data
        UsuarioRegistroDto unregisteredUserDto = new UsuarioRegistroDto(
                "Michael",
                "Jordan",
                "mj@me.com",
                "somepassword"
        );
        UsuarioRegistroResponseDto responseDto = new UsuarioRegistroResponseDto(
                "Usuario creado satisfactoriamente"
        );

        // Setup - expectations
        given(service.registrarUsuario(unregisteredUserDto)).willReturn(responseDto);

        // exercise
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.
                    post("/registro")
                    .content(asJsonString(unregisteredUserDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL))
                .andReturn().getResponse();

        // verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        Assertions.assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        Assertions.assertThat(response.getContentAsString()).isEqualTo(asJsonString(responseDto));
    }

    @Test
    public void testRegisteringANewUserWithAWrongEmailReturnsBadRequest() throws Exception {
        // Setup - data
        UsuarioRegistroDto unregisteredUserDto = new UsuarioRegistroDto(
                "Michael",
                "Jordan",
                "notEmail",
                "somepassword"
        );

        // exercise
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.
                        post("/registro")
                        .content(asJsonString(unregisteredUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();

        // verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testRegisteringANewUserWithEmptyNombreReturnsBadRequest() throws Exception {
        // Setup - data
        UsuarioRegistroDto unregisteredUserDto = new UsuarioRegistroDto(
                "",
                "Jordan",
                "mj@me.com",
                "somepassword"
        );

        // exercise
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.
                        post("/registro")
                        .content(asJsonString(unregisteredUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();

        // verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @Test
    public void testRegisteringANewUserWithEmptyApellidoReturnsBadRequest() throws Exception {
        // Setup - data
        UsuarioRegistroDto unregisteredUserDto = new UsuarioRegistroDto(
                "Michael",
                "",
                "mj@me.com",
                "somepassword"
        );

        // exercise
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.
                        post("/registro")
                        .content(asJsonString(unregisteredUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();

        // verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testRegisteringANewUserWithEmptyEmailReturnsBadRequest() throws Exception {
        // Setup - data
        UsuarioRegistroDto unregisteredUserDto = new UsuarioRegistroDto(
                "Michael",
                "Jordan",
                "",
                "somepassword"
        );

        // exercise
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.
                        post("/registro")
                        .content(asJsonString(unregisteredUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();

        // verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testRegisteringANewUserWithEmptyPasswordReturnsBadRequest() throws Exception {
        // Setup - data
        UsuarioRegistroDto unregisteredUserDto = new UsuarioRegistroDto(
                "Michael",
                "Jordan",
                "mj@me.com",
                ""
        );

        // exercise
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.
                        post("/registro")
                        .content(asJsonString(unregisteredUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andReturn().getResponse();

        // verify
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

//    @Test
//    public void testGettingFormularioRegistroIsSuccessful(){
//        // exercise
//        String formularioRegistro = controller.mostrarFormularioRegistro();
//
//        // verify
//        Assertions.assertThat(formularioRegistro).isEqualTo("registroTemplate");
//    }
//
//    @Test
//    public void testRegisteringMichaelJordanIsSuccessful(){
//        // Setup - data
//        UsuarioRegistroDto unregisteredUserDto = new UsuarioRegistroDto(
//                "Michael",
//                "Jordan",
//                "mj@me.com",
//                "somepassword"
//        );
//        Usuario registeredUser = new Usuario(
//                "Michael",
//                "Jordan",
//                "mj@me.com",
//                "somepassword"
//        );
//
//        // Setup - expectations
//        given(service.save(unregisteredUserDto)).willReturn(registeredUser);
//
//        // exercise
//        String serverResponse = controller.registrarCuentaDeUsuario(unregisteredUserDto);
//
//        // verify
//        Assertions.assertThat(serverResponse).isEqualTo("redirect:/registro?success");
//    }

}