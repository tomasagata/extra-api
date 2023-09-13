package org.mojodojocasahouse.extra.service;

import org.mojodojocasahouse.extra.dto.ExtraUserRegistrationDto;
import org.mojodojocasahouse.extra.dto.ExtraUserRegistrationResponseDto;


public interface ExtraUserService {

    ExtraUserRegistrationResponseDto registrarUsuario(ExtraUserRegistrationDto userRegistrationDto);
}
