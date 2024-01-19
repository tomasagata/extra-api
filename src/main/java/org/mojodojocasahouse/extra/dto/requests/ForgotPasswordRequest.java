package org.mojodojocasahouse.extra.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @NotBlank(message = "Email must not be left blank")
    @NotNull(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    private String email;

    public ForgotPasswordRequest(String email){
        this.email = email;
    }

}
