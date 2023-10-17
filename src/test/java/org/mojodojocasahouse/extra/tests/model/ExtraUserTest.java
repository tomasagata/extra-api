package org.mojodojocasahouse.extra.tests.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.model.Authority;
import org.mojodojocasahouse.extra.model.ExtraUser;

import java.util.Set;


class ExtraUserTest {

    @Test
    void testGettingIdOfMichaelJordanReturnsNull() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        Assertions.assertThat(unmanagedUser.getId()).isNull();
    }

    @Test
    void testGettingFirstNameOfMichaelJordanReturnsMichael() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        Assertions.assertThat(unmanagedUser.getFirstName()).isEqualTo("Michael");
    }

    @Test
    void testGettingLastNameOfMichaelJordanReturnsJordan() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        Assertions.assertThat(unmanagedUser.getLastName()).isEqualTo("Jordan");
    }

    @Test
    void testGettingEmailOfMichaelJordanReturnsMichaelJAtGmailDotCom() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        Assertions.assertThat(unmanagedUser.getEmail()).isEqualTo("michaelj@gmail.com");
    }

    @Test
    void testGettingPasswordOfMichaelJordanReturnsSomePassword() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        Assertions.assertThat(unmanagedUser.getPassword()).isEqualTo("somepassword");
    }

    @Test
    void testGettingAuthoritiesOfMichaelJordanReturnsSetOfUser() {
        Authority userAuthority = new Authority("USER");
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword",
                Set.of(userAuthority)
        );

        Assertions.assertThat(unmanagedUser.getAuthorities()).containsExactlyInAnyOrder(userAuthority);
    }

    @Test
    void testSettingFirstNameOfMichaelJordanToMikeActuallyChangesIt() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        unmanagedUser.setFirstName("Mike");

        Assertions.assertThat(unmanagedUser.getFirstName()).isEqualTo("Mike");
    }

    @Test
    void testSettingLastNameOfMichaelJordanToScottActuallyChangesIt() {
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        unmanagedUser.setLastName("Scott");

        Assertions.assertThat(unmanagedUser.getLastName()).isEqualTo("Scott");
    }

    @Test
    void testSettingEmailOfMichaelJordanToMJAtMeDotComActuallyChangesIt() {
        ExtraUser unmanagedUser = new ExtraUser(
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
        ExtraUser unmanagedUser = new ExtraUser(
                "Michael",
                "Jordan",
                "michaelj@gmail.com",
                "somepassword"
        );

        unmanagedUser.setPassword("anotherpassword");

        Assertions.assertThat(unmanagedUser.getPassword()).isEqualTo("anotherpassword");
    }
}