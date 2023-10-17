package org.mojodojocasahouse.extra.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.mojodojocasahouse.extra.validation.constraint.FieldsValueMatch;

import java.util.UUID;

@Data
@FieldsValueMatch(
        field = "newPassword",
        fieldMatch = "newPasswordRepeat",
        message = "New password and new password repeat must match"
)
public class PasswordResetRequest {

    @NotNull(message = "Token is mandatory")
    private UUID token;

    @NotNull(message = "New password is mandatory")
    @Size(max = 100, message = "Password cannot exceed 100 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?]{8,}$", message = "New password must contain eight characters: one letter, one number and one of the following: @$!%*#?")
    private String newPassword;

    @NotNull(message = "New password repeat is mandatory")
    private String newPasswordRepeat;

    public PasswordResetRequest(UUID token, String newPassword, String newPasswordRepeat){
        this.token = token;
        this.newPassword = newPassword;
        this.newPasswordRepeat = newPasswordRepeat;
    }

    public PasswordResetRequest(){}

}
