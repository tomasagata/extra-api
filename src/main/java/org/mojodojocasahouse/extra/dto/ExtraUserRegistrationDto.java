package org.mojodojocasahouse.extra.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ExtraUserRegistrationDto {

    @NotBlank
    @NotNull
    private String firstName;
    @NotBlank
    @NotNull
    private String lastName;
    @Email
    @NotBlank
    @NotNull
    private String email;
    @NotBlank
    @NotNull
    private String password;

    public ExtraUserRegistrationDto(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public ExtraUserRegistrationDto(){}

}
