package org.mojodojocasahouse.extra.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserAuthenticationRequest {

    @NotBlank(message = "Email must not be left blank")
    @NotNull(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Password is mandatory")
    @Pattern(regexp = "^([A-Za-z]|\\d|[@$!%*#?&])+$", message = "Password can only contain letters, numbers or the following: @$!%*#?")
    private String password;

    public UserAuthenticationRequest() {
    }
    public UserAuthenticationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}