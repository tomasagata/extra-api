package org.mojodojocasahouse.extra.tests.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.exception.InvalidPasswordResetTokenException;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.model.PasswordResetToken;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.repository.PasswordResetTokenRepository;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.testmodels.TestPasswordResetToken;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private ExtraUserRepository repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordResetTokenRepository tokenRepo;

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
        given(passwordEncoder.encode(any())).willReturn("somepassword");
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
    public void testGettingUserByPrincipalIsSuccessful() {
        // Setup - data
        ExtraUser existingUser = new ExtraUser(
                "Some",
                "User",
                "mj@me.com",
                "a_hashed_password"
        );
        Principal mockPrincipal = Mockito.mock(Principal.class);

        // Setup - expectations
        given(mockPrincipal.getName()).willReturn("mj@me.com");
        given(repo.findByEmail(any(String.class))).willReturn(Optional.of(existingUser));

        ExtraUser foundUser = serv.getUserByPrincipal(mockPrincipal);

        // exercise and verify
        Assertions.assertThat(foundUser).isEqualTo(existingUser);
    }

    @Test
    public void testGettingUserByUnknownPrincipalIsSuccessful() {
        // Setup - data
        Principal mockPrincipal = Mockito.mock(Principal.class);

        // Setup - expectations
        given(mockPrincipal.getName()).willReturn("mj@me.com");
        given(repo.findByEmail(any(String.class))).willReturn(Optional.empty());


        // exercise and verify
        Assertions
                .assertThatException()
                .isThrownBy(() -> serv.getUserByPrincipal(mockPrincipal))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testChangingPasswordIsSuccessful() {
        // Setup - data
        ExtraUser existingUser = new ExtraUser(
                "Some",
                "User",
                "mj@me.com",
                "curr_pass_hashed"
        );
        UserChangePasswordRequest request = new UserChangePasswordRequest(
                "curr_pass",
                "new_pass",
                "new_pass"
        );
        ApiResponse expectedResponse = new ApiResponse("Password changed successfully");

        // Setup - expectations
        given(passwordEncoder.encode("curr_pass")).willReturn("curr_pass_hashed");
        given(passwordEncoder.encode("new_pass")).willReturn("new_pass_hashed");
        given(repo.findOneByEmailAndPassword(any(), any())).willReturn(Optional.of(existingUser));

        ApiResponse response = serv.changePassword(existingUser, request);

        // exercise and verify
        Assertions.assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    public void testChangingPasswordFailsWhenPasswordsDontMatch() {
        // Setup - data
        ExtraUser existingUser = new ExtraUser(
                "Some",
                "User",
                "mj@me.com",
                "curr_pass_hashed"
        );
        UserChangePasswordRequest request = new UserChangePasswordRequest(
                "some_pass",
                "new_pass",
                "new_pass"
        );
        ApiResponse expectedResponse = new ApiResponse("Password changed successfully");

        // Setup - expectations
        given(passwordEncoder.encode("some_pass")).willReturn("some_pass_hashed");
        given(passwordEncoder.encode("new_pass")).willReturn("new_pass_hashed");
        given(repo.findOneByEmailAndPassword(any(), any())).willReturn(Optional.empty());



        // exercise and verify
        Assertions
                .assertThatException()
                .isThrownBy(() -> serv.changePassword(existingUser, request))
                .isInstanceOf(BadCredentialsException.class);
    }

//    @Test
//    public void testSendingPasswordResetEmailIsSuccessful() throws MessagingException {
//        // Setup - data
//        ExtraUser existingUser = new ExtraUser(
//                "Some",
//                "User",
//                "mj@me.com",
//                "curr_pass_hashed"
//        );
//        ForgotPasswordRequest request = new ForgotPasswordRequest(
//                "mj@me.com"
//        );
//        PasswordResetToken token = new PasswordResetToken(existingUser);
//        MimeMessage mockMessage = Mockito.mock(MimeMessage.class);
//
//        // Setup - expectations
//        given(repo.findByEmail(any())).willReturn(Optional.of(existingUser));
//        given(tokenRepo.save(any())).willReturn(token);
//        given(mailSender.createMimeMessage()).willReturn(mockMessage);
//
//        doNothing().when(mockMessage).setFrom(anyString());
//        doNothing().when(mockMessage).setRecipient(Message.RecipientType.TO, any());
//        doNothing().when(mockMessage).setSubject(anyString());
//        doNothing().when(mockMessage).setContent(any(), any());
//
//        doNothing().when(mailSender).send(any(MimeMessage.class));
//
//        // exercise and verify
//        Assertions.assertThatNoException().isThrownBy(() -> serv.sendPasswordResetEmail(request));
//    }

    @Test
    public void testSendingPasswordResetEmailFailsSilentlyWhenNoUserIsFound() {
        // Setup - data
        ForgotPasswordRequest request = new ForgotPasswordRequest(
                "another@email.com"
        );

        // Setup - expectations
        given(repo.findByEmail(any())).willReturn(Optional.empty());

        serv.sendPasswordResetEmail(request);

        // exercise and verify
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
        verify(repo, never()).save(any());
    }

    @Test
    public void testResettingPasswordIsSuccessful() {
        ExtraUser user = new ExtraUser(
                "Some",
                "User",
                "mj@me.com",
                "curr_pass_hashed"
        );
        PasswordResetRequest request = new PasswordResetRequest(
                UUID.fromString("48bb4ed9-5c2f-4892-bf1b-2b77d3a6bf34"),
                "new_pass",
                "new_pass"
        );
        PasswordResetToken token = new TestPasswordResetToken(
                UUID.fromString("48bb4ed9-5c2f-4892-bf1b-2b77d3a6bf34"),
                user,
                100
        );

        given(tokenRepo.findById(any())).willReturn(Optional.of(token));
        given(passwordEncoder.encode("new_pass")).willReturn("new_pass_hashed");
        given(repo.save(any(ExtraUser.class))).willReturn(null);

        Assertions.assertThatNoException().isThrownBy(() -> serv.resetPassword(request));

    }

    @Test
    public void testResettingPasswordWithInvalidTokenThrowsError() {
        PasswordResetRequest request = new PasswordResetRequest(
                UUID.fromString("48bb4ed9-5c2f-4892-bf1b-2b77d3a6bf34"),
                "new_pass",
                "new_pass"
        );

        given(tokenRepo.findById(any())).willReturn(Optional.empty());

        verify(passwordEncoder, never()).encode(any());
        verify(repo, never()).save(any());
        Assertions
                .assertThatException()
                .isThrownBy(() -> serv.resetPassword(request))
                .isInstanceOf(InvalidPasswordResetTokenException.class);

    }

}
