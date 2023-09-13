package org.mojodojocasahouse.extra.model;

import org.mojodojocasahouse.extra.model.impl.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class UsuarioTest {

    @Test
    void testGettingIdOfMichaelJordanReturnsNull() {
        Usuario unmanagedUser = new Usuario(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        Assertions.assertThat(unmanagedUser.getId()).isNull();
    }

    @Test
    void testGettingNombreOfMichaelJordanReturnsMichael() {
        Usuario unmanagedUser = new Usuario(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        Assertions.assertThat(unmanagedUser.getNombre()).isEqualTo("Michael");
    }

    @Test
    void testGettingApellidoOfMichaelJordanReturnsJordan() {
        Usuario unmanagedUser = new Usuario(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        Assertions.assertThat(unmanagedUser.getApellido()).isEqualTo("Jordan");
    }

    @Test
    void testGettingEmailOfMichaelJordanReturnsMichaelJAtGmailDotCom() {
        Usuario unmanagedUser = new Usuario(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        Assertions.assertThat(unmanagedUser.getEmail()).isEqualTo("michaelj@gmail.com");
    }

    @Test
    void testGettingPasswordOfMichaelJordanReturnsSomePassword() {
        Usuario unmanagedUser = new Usuario(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        Assertions.assertThat(unmanagedUser.getPassword()).isEqualTo("somepassword");
    }

    @Test
    void testSettingNombreOfMichaelJordanToMikeActuallyChangesIt() {
        Usuario unmanagedUser = new Usuario(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        unmanagedUser.setNombre("Mike");

        Assertions.assertThat(unmanagedUser.getNombre()).isEqualTo("Mike");
    }

    @Test
    void testSettingApellidoOfMichaelJordanToScottActuallyChangesIt() {
        Usuario unmanagedUser = new Usuario(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        unmanagedUser.setApellido("Scott");

        Assertions.assertThat(unmanagedUser.getApellido()).isEqualTo("Scott");
    }

    @Test
    void testSettingEmailOfMichaelJordanToMJAtMeDotComActuallyChangesIt() {
        Usuario unmanagedUser = new Usuario(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        unmanagedUser.setEmail("mj@me.com");

        Assertions.assertThat(unmanagedUser.getEmail()).isEqualTo("mj@me.com");
    }

    @Test
    void testSettingPasswordOfMichaelJordanToAnotherPasswordActuallyChangesIt() {
        Usuario unmanagedUser = new Usuario(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        unmanagedUser.setPassword("anotherpassword");

        Assertions.assertThat(unmanagedUser.getPassword()).isEqualTo("anotherpassword");
    }
}