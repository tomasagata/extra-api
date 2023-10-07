package org.mojodojocasahouse.extra.service;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.model.*;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ExtraUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


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

}
