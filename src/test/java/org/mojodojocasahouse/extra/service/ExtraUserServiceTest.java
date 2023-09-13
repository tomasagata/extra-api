package org.mojodojocasahouse.extra.service;

import org.mojodojocasahouse.extra.dto.ExtraUserRegistrationDto;
import org.mojodojocasahouse.extra.dto.ExtraUserRegistrationResponseDto;
import org.mojodojocasahouse.extra.model.impl.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.mojodojocasahouse.extra.service.impl.ExtraUserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ExtraUserServiceTest {

    @Mock
    private ExtraUserRepository repo;

    @InjectMocks
    private ExtraUserServiceImpl serv;

    @Test
    public void testPassingAUserRegistrationRequestReturnsASuccessfulResponse() {
        // Setup - data
        ExtraUser mj = new ExtraUser(
                "Michael",
                "Jordan",
                "mj@me.com",
                "somepassword"
        );
        ExtraUserRegistrationDto mjDto = new ExtraUserRegistrationDto(
                "Michael",
                "Jordan",
                "mj@me.com",
                "somepassword");
        ExtraUserRegistrationResponseDto successfulResponse= new ExtraUserRegistrationResponseDto(
                "ExtraUser created successfully"
        );

        // Setup – expectations
        given(repo.save(any(ExtraUser.class))).willReturn(mj);

        // exercise
        ExtraUserRegistrationResponseDto response = serv.registrarUsuario(mjDto);

        // verify
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response).isEqualTo(successfulResponse);
    }

}
