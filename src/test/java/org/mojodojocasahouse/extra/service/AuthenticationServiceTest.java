package org.mojodojocasahouse.extra.service;

import org.mojodojocasahouse.extra.dto.UserAuthenticationRequest;
import org.mojodojocasahouse.extra.dto.UserAuthenticationResponse;
import org.mojodojocasahouse.extra.dto.UserRegistrationRequest;
import org.mojodojocasahouse.extra.dto.UserRegistrationResponse;
import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.exception.InvalidCredentialsException;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private ExtraUserRepository repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService serv;

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
        given(passwordEncoder.encode(any(String.class))).willReturn(mj.getPassword());

        // exercise
        UserRegistrationResponse response = serv.registerUser(mjDto);

        // verify
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response).isEqualTo(successfulResponse);
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

    @Test
    public void testAuthenticatingAsARegisteredUserReturnsSuccessfulResponse(){
        // Setup - data
        UserAuthenticationRequest request = new UserAuthenticationRequest(
                "mj@me.com",
                "a_password"
        );
        ExtraUser existingUser = new ExtraUser(
                "Some",
                "User",
                "mj@me.com",
                "a_hashed_password"
        );
        UserAuthenticationResponse expectedResponse = new UserAuthenticationResponse("Login Success", true);

        // Setup - expectations
        given(repo.findOneByEmailAndPassword(any(String.class), any(String.class))).willReturn(Optional.of(existingUser));
        given(passwordEncoder.encode(any(String.class))).willReturn("a_hashed_password");

        // exercise
        UserAuthenticationResponse response = serv.authenticateUser(request);

        // verify
        Assertions.assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    public void testAuthenticatingAsAnUnregisteredUserThrowsInvalidCredentialsException(){
        // Setup - data
        UserAuthenticationRequest request = new UserAuthenticationRequest(
                "mj@me.com",
                "a_password"
        );

        // Setup - expectations
        given(repo.findOneByEmailAndPassword(any(String.class), any(String.class))).willReturn(Optional.empty());
        given(passwordEncoder.encode(any(String.class))).willReturn("a_hashed_password");

        // exercise and verify
        Assertions
                .assertThatThrownBy(() -> serv.authenticateUser(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid Authentication Credentials");
    }
}
