package org.mojodojocasahouse.extra.service.impl;

import jakarta.validation.ConstraintViolationException;
import org.mojodojocasahouse.extra.dto.ExtraUserRegistrationDto;
import org.mojodojocasahouse.extra.dto.ExtraUserRegistrationResponseDto;
import org.mojodojocasahouse.extra.model.impl.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.mojodojocasahouse.extra.service.ExtraUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExtraUserServiceImpl implements ExtraUserService {

    private final ExtraUserRepository userRepository;

    @Autowired
    public ExtraUserServiceImpl(ExtraUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ExtraUserRegistrationResponseDto registrarUsuario(ExtraUserRegistrationDto userRegistrationDto)
                                                            throws ConstraintViolationException {
        ExtraUser usuario = new ExtraUser(userRegistrationDto.getFirstName(), userRegistrationDto.getLastName(), userRegistrationDto.getEmail(), userRegistrationDto.getPassword());
        ExtraUser registeredUser = userRepository.save(usuario);
        // Do something with registered user
        return new ExtraUserRegistrationResponseDto("ExtraUser created successfully");
    }
}
