package org.mojodojocasahouse.extra.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserAuthenticationRequest {

    @NotBlank(message = "Email must not be left blank")
    @NotNull(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Password is mandatory")
    @Pattern(regexp = "^([A-Za-z]|\\d|[@$!%*#?])+$", message = "Password can only contain letters, numbers or the following: @$!%*#?")
    private String password;

    private Boolean rememberMe = false;

    public UserAuthenticationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserAuthenticationRequest(String email, String password, Boolean rememberMe) {
        this.email = email;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    public UserAuthenticationRequest() {
    }
}