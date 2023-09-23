package org.mojodojocasahouse.extra.service;

import jakarta.servlet.http.Cookie;
import org.apache.commons.codec.digest.DigestUtils;
import org.mojodojocasahouse.extra.dto.UserAuthenticationRequest;
import org.mojodojocasahouse.extra.dto.UserAuthenticationResponse;
import org.mojodojocasahouse.extra.dto.UserRegistrationRequest;
import org.mojodojocasahouse.extra.dto.UserRegistrationResponse;
import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.exception.InvalidCredentialsException;
import org.mojodojocasahouse.extra.exception.InvalidSessionTokenException;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.model.SessionToken;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.mojodojocasahouse.extra.repository.SessionTokenRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationService {

    private final ExtraUserRepository userRepository;

    private final SessionTokenRepository sessionRepository;

    public AuthenticationService(ExtraUserRepository userRepository, SessionTokenRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public UserRegistrationResponse registerUser(UserRegistrationRequest userRegistrationRequest)
        throws ExistingUserEmailException {

        // validate email
        validateEmailUniqueness(userRegistrationRequest);

        // Create encoded password
        String encodedPassword = DigestUtils.sha256Hex(userRegistrationRequest.getPassword());

        // create user entity from request data
        ExtraUser newUser = ExtraUser.from(userRegistrationRequest, encodedPassword);

        // Save new user
        ExtraUser savedUser = userRepository.save(newUser);

        return new UserRegistrationResponse("User created successfully");
    }


    public Pair<UserAuthenticationResponse, Cookie> authenticateUser(UserAuthenticationRequest userAuthenticationRequest)
            throws InvalidCredentialsException{

        // Encode receiving password
        String encodedPassword = DigestUtils.sha256Hex(userAuthenticationRequest.getPassword());

        // Get user from database with credentials or else throw InvalidCredentialsException
        ExtraUser existingUser = userRepository
                .findOneByEmailAndPassword(
                        userAuthenticationRequest.getEmail(),
                        encodedPassword
                ).orElseThrow(InvalidCredentialsException::new);

        Cookie cookie = createNewSession(existingUser);

        // Return successful response if user found
        return Pair.of(
                new UserAuthenticationResponse("Login Success"),
                cookie
        );
    }

    public Cookie createNewSession(ExtraUser linkedUser){
        UUID sessionId = sessionRepository.save(
                new SessionToken(linkedUser)
        ).getId();

        return new Cookie("JSESSIONID", sessionId.toString());
    }

    public void validateAuthentication(UUID sessionId) throws InvalidSessionTokenException {
        SessionToken token = sessionRepository.findById(sessionId).orElseThrow(InvalidSessionTokenException::new);
        token.validate();
    }

//    public void validateAuthorization(UUID sessionId) throws AccessForbiddenException {
//    }

    private void validateEmailUniqueness(UserRegistrationRequest userRequest) throws ExistingUserEmailException{
        userRepository
                .findByEmail(userRequest.getEmail())
                .ifPresent(
                        s -> {throw new ExistingUserEmailException();}
                );
    }


}
