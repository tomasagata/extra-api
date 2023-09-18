package org.mojodojocasahouse.extra.service;

import org.mojodojocasahouse.extra.dto.UserRegistrationRequest;
import org.mojodojocasahouse.extra.dto.UserRegistrationResponse;
import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.exception.MismatchingPasswordsException;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.mojodojocasahouse.extra.service.impl.ExtraUserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ExtraUserServiceTest {

    @Mock
    private ExtraUserRepository repo;

    @InjectMocks
    private ExtraUserServiceImpl serv;

    @Test
    public void testPassingAUserRegistrationRequestReturnsASuccessfulResponse() {
        // Setup - data
        ExtraUser mj = new ExtraUser(
                "Michael",
                "Jordan",
                "mj@me.com",
                "somepassword"
        );
        UserRegistrationRequest mjDto = new UserRegistrationRequest(
                "Michael",
                "Jordan",
                "mj@me.com",
                "somepassword",
                "somepassword"
        );
        UserRegistrationResponse successfulResponse= new UserRegistrationResponse(
                "User created successfully"
        );

        // Setup – expectations
        given(repo.save(any(ExtraUser.class))).willReturn(mj);

        // exercise
        UserRegistrationResponse response = serv.registerUser(mjDto);

        // verify
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response).isEqualTo(successfulResponse);
    }

    @Test
    public void testRegisteringAUserWithMismatchingPasswordsThrowsMismatchingPasswordsException() {
        // Setup - data
        UserRegistrationRequest mjDto = new UserRegistrationRequest(
                "Michael",
                "Jordan",
                "mj@me.com",
                "some_password",
                "other_password"
        );

        // exercise and verify
        Assertions
                .assertThatThrownBy(() -> serv.registerUser(mjDto))
                .isInstanceOf(MismatchingPasswordsException.class)
                .hasMessage("Passwords must match");
    }

    @Test
    public void testRegisteringAUserWithAnEmailAlreadyInUseThrowsExistingUserEmailException() {
        // Setup - data
        UserRegistrationRequest mjDto = new UserRegistrationRequest(
                "Michael",
                "Jordan",
                "mj@me.com",
                "some_password",
                "some_password"
        );
        ExtraUser existingUser = new ExtraUser(
                "Some",
                "User",
                "mj@me.com",
                "a_password"
        );

        // Setup - expectations
        given(repo.findByEmail(any(String.class))).willReturn(Optional.of(existingUser));

        // exercise and verify
        Assertions
                .assertThatThrownBy(() -> serv.registerUser(mjDto))
                .isInstanceOf(ExistingUserEmailException.class)
                .hasMessage("User email already registered");
    }

}
