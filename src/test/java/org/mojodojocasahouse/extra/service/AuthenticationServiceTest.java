package org.mojodojocasahouse.extra.service;

import jakarta.servlet.http.Cookie;
import org.apache.commons.codec.digest.DigestUtils;
import org.mojodojocasahouse.extra.dto.UserAuthenticationRequest;
import org.mojodojocasahouse.extra.dto.UserAuthenticationResponse;
import org.mojodojocasahouse.extra.dto.UserRegistrationRequest;
import org.mojodojocasahouse.extra.dto.UserRegistrationResponse;
import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.exception.InvalidCredentialsException;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.model.SessionToken;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.repository.SessionTokenRepository;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private ExtraUserRepository repo;

    @Mock
    private SessionTokenRepository tokenRepository;

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
                "a_hashed_password"
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
                DigestUtils.sha256Hex(request.getPassword())
        );
        SessionToken token = new SessionToken(
                UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
                existingUser
        );
        UserAuthenticationResponse expectedResponse = new UserAuthenticationResponse(
                "Login Success"
        );
        Cookie expectedCookie = new Cookie(
                "JSESSIONID",
                "123e4567-e89b-12d3-a456-426655440000"
        );
        Pair<UserAuthenticationResponse, Cookie> expectedResponseCookiePair = Pair.of(expectedResponse, expectedCookie);

        // Setup - expectations
        given(repo.findOneByEmailAndPassword(any(String.class), any(String.class))).willReturn(Optional.of(existingUser));
        given(tokenRepository.save(any(SessionToken.class))).willReturn(token);

        // exercise
        Pair<UserAuthenticationResponse, Cookie> actualResponseCookiePair = serv.authenticateUser(request);

        // verify
        Assertions.assertThat(actualResponseCookiePair).isEqualTo(expectedResponseCookiePair);
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

        // exercise and verify
        Assertions
                .assertThatThrownBy(() -> serv.authenticateUser(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid Authentication Credentials");
    }
}
