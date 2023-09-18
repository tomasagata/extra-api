package org.mojodojocasahouse.extra.service;

import org.mojodojocasahouse.extra.dto.UserRegistrationRequest;
import org.mojodojocasahouse.extra.dto.UserRegistrationResponse;
import org.mojodojocasahouse.extra.exception.MismatchingPasswordsException;


public interface ExtraUserService {

    UserRegistrationResponse registerUser(UserRegistrationRequest userRegistrationDto) throws MismatchingPasswordsException;
}
