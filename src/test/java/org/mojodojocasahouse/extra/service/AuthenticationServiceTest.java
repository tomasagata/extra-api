package org.mojodojocasahouse.extra.service;

import jakarta.servlet.http.Cookie;
import org.apache.commons.codec.digest.DigestUtils;
import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.exception.InvalidCredentialsException;
import org.mojodojocasahouse.extra.exception.InvalidSessionTokenException;
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

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

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
        ApiResponse successfulResponse= new ApiResponse(
                "User created successfully"
        );

        // Setup – expectations
        given(repo.save(any(ExtraUser.class))).willReturn(mj);

        // exercise
        ApiResponse response = serv.registerUser(mjDto);

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
        ApiResponse expectedResponse = new ApiResponse(
                "Login Success"
        );
        Cookie expectedCookie = new Cookie(
                "JSESSIONID",
                "123e4567-e89b-12d3-a456-426655440000"
        );
        Pair<ApiResponse, Cookie> expectedResponseCookiePair = Pair.of(expectedResponse, expectedCookie);

        // Setup - expectations
        given(repo.findOneByEmailAndPassword(any(String.class), any(String.class))).willReturn(Optional.of(existingUser));
        given(tokenRepository.save(any(SessionToken.class))).willReturn(token);

        // exercise
        Pair<ApiResponse, Cookie> actualResponseCookiePair = serv.authenticateUser(request);

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

    @Test
    public void testValidatingSessionTokenOfExistingSessionThrowsNothing(){
        // Setup - data
        UUID validSessionId = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jordan",
                "mj@me.com",
                "somepassword"
        );
        SessionToken linkedSessionToken = new SessionToken(
                validSessionId,
                user
        );

        // Setup - expectations
        given(tokenRepository.findById(any())).willReturn(Optional.of(linkedSessionToken));

        // assertions
        Assertions.assertThatNoException().isThrownBy(() -> serv.validateAuthentication(validSessionId));

    }

    @Test
    public void testValidatingSessionTokenOfNonExistingSessionThrowsInvalidSessionTokenException(){
        // Setup - data
        UUID validSessionId = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");

        // Setup - expectations
        given(tokenRepository.findById(any())).willReturn(Optional.empty());

        // assertions
        Assertions.assertThatThrownBy(() -> serv.validateAuthentication(validSessionId)).isInstanceOf(InvalidSessionTokenException.class);

    }

    @Test
    public void testGettingUserByValidSessionIdReturnsUser(){
        // Setup - data
        UUID validSessionId = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jordan",
                "mj@me.com",
                "somepassword"
        );
        SessionToken linkedSessionToken = new SessionToken(
                validSessionId,
                user
        );

        // Setup - expectations
        given(tokenRepository.findById(any())).willReturn(Optional.of(linkedSessionToken));

        // exercise
        ExtraUser foundUser = serv.getUserBySessionToken(validSessionId);

        // verify
        Assertions.assertThat(foundUser).isEqualTo(user);
    }

    @Test
    public void testGettingUserByInvalidSessionIdThrowsInvalidSessionTokenException(){
        // Setup - data
        UUID validSessionId = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");

        // Setup - expectations
        given(tokenRepository.findById(any())).willReturn(Optional.empty());

        // exercise and verify
        Assertions.assertThatThrownBy(() -> serv.getUserBySessionToken(validSessionId)).isInstanceOf(InvalidSessionTokenException.class);
    }
}
