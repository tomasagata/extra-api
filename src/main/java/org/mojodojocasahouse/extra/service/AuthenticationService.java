package org.mojodojocasahouse.extra.service;

import org.mojodojocasahouse.extra.dto.UserAuthenticationRequest;
import org.mojodojocasahouse.extra.dto.UserAuthenticationResponse;
import org.mojodojocasahouse.extra.dto.UserRegistrationRequest;
import org.mojodojocasahouse.extra.dto.UserRegistrationResponse;
import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.exception.InvalidCredentialsException;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final ExtraUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(ExtraUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRegistrationResponse registerUser(UserRegistrationRequest userRegistrationRequest)
        throws ExistingUserEmailException {

        // validate email
        validateEmailUniqueness(userRegistrationRequest);

        // Create encoded password
        String encodedPassword = passwordEncoder.encode(userRegistrationRequest.getPassword());

        // create user entity from request data
        ExtraUser newUser = ExtraUser.from(userRegistrationRequest, encodedPassword);

        // Save new user
        ExtraUser savedUser = userRepository.save(newUser);

        return new UserRegistrationResponse("User created successfully");
    }


    public UserAuthenticationResponse authenticateUser(UserAuthenticationRequest userAuthenticationRequest)
            throws InvalidCredentialsException{

        // Encode receiving password
        String encodedPassword = passwordEncoder.encode(userAuthenticationRequest.getPassword());

        // Get user from database with credentials or else throw InvalidCredentialsException
        ExtraUser existingUser = userRepository
                .findOneByEmailAndPassword(
                        userAuthenticationRequest.getEmail(),
                        encodedPassword
                ).orElseThrow(InvalidCredentialsException::new);

        // Return successful response if user found
        return new UserAuthenticationResponse("Login Success", true);
    }

    private void validateEmailUniqueness(UserRegistrationRequest userRequest) throws ExistingUserEmailException{
        userRepository
                .findByEmail(userRequest.getEmail())
                .ifPresent(
                        s -> {throw new ExistingUserEmailException();}
                );
    }


}
