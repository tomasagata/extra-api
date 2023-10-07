package org.mojodojocasahouse.extra.service;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.exception.InvalidPasswordResetTokenException;
import org.mojodojocasahouse.extra.model.*;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.mojodojocasahouse.extra.repository.PasswordResetTokenRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ExtraUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender mailSender;

    private final PasswordResetTokenRepository tokenRepository;


    public ApiResponse registerUser(UserRegistrationRequest userRegistrationRequest)
        throws ExistingUserEmailException {

        // validate email
        validateEmailUniqueness(userRegistrationRequest);

        // Create encoded password
        String encodedPassword = passwordEncoder.encode(userRegistrationRequest.getPassword());

        // create user entity from request data
        ExtraUser newUser = ExtraUser.from(userRegistrationRequest, encodedPassword);

        // Save new user
        userRepository.save(newUser);

        return new ApiResponse("User created successfully");
    }

    public ExtraUser getUserByPrincipal(Principal principal) throws RuntimeException{
        return userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
    }


    private void validateEmailUniqueness(UserRegistrationRequest userRequest) throws ExistingUserEmailException{
        userRepository
                .findByEmail(userRequest.getEmail())
                .ifPresent(s -> {throw new ExistingUserEmailException();});
    }

    public ApiResponse changePassword(ExtraUser user, @Valid UserChangePasswordRequest userChangePasswordRequest) {
        // Function that given the correct old password, sets a new one for the given user
        // if the old password is incorrect, it returns an error
        String newPassword = passwordEncoder.encode(userChangePasswordRequest.getNewPassword());
        String encodedPassword = passwordEncoder.encode(userChangePasswordRequest.getCurrentPassword());
        ExtraUser changingUser = userRepository
                .findOneByEmailAndPassword(user.getEmail(),encodedPassword)
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));
        changingUser.setPassword(newPassword);
        userRepository.save(changingUser);

        return new ApiResponse("Password changed successfully");
    }

    public ApiResponse sendPasswordResetEmail(ForgotPasswordRequest request){
        Optional<ExtraUser> foundUser = userRepository.findByEmail(request.getEmail());

        if(foundUser.isPresent()){
            ExtraUser user = foundUser.get();
            PasswordResetToken token = tokenRepository.save(
                    new PasswordResetToken(user)
            );

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply.extraapp@gmail.com");
            message.setTo(user.getEmail());
            message.setSubject("Extraapp - Password recovery");
            message.setText(
                    "Please tap the following link on your phone " +
                    "with the app installed to reset your password: \n" +
                            "extra://reset-password/" + token.getId() + " \nLink is valid for only 15 minutes.");

            mailSender.send(message);
        }

        return new ApiResponse("If user is registered, an email was sent. Check inbox");
    }

    public ApiResponse resetPassword(PasswordResetRequest request) throws InvalidPasswordResetTokenException{
        PasswordResetToken
                token = tokenRepository
                            .findById(request.getToken())
                            .orElseThrow(InvalidPasswordResetTokenException::new);

        token.assertValid();
        String newPassword = passwordEncoder.encode(request.getNewPassword());
        ExtraUser changingUser = token.getUser();
        changingUser.setPassword(newPassword);
        userRepository.save(changingUser);

        return new ApiResponse("Password changed successfully");
    }

}
