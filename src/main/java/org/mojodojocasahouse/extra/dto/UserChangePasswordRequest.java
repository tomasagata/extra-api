package org.mojodojocasahouse.extra.dto;


import jakarta.validation.constraints.*;
import lombok.Data;
import org.mojodojocasahouse.extra.validation.constraint.FieldsValueMatch;

@Data
@FieldsValueMatch(
        field = "newPassword",
        fieldMatch = "newPasswordRepeat",
        message = "New password and new password repeat must match"
)
public class UserChangePasswordRequest {
    @NotNull(message = "Last Password is mandatory")
    @Pattern(regexp = "^([A-Za-z]|\\d|[@$!%*#?])+$", message = "Password can only contain letters, numbers or the following: @$!%*#?")
    private String LastPassword;

    @NotNull(message = "New Password is mandatory")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?]{8,}$", message = "New password must contain eight characters: one letter, one number and one of the following: @$!%*#?")
    private String newPassword;

    @NotNull(message = "New password repeat is mandatory")
    private String newPasswordRepeat;


    public UserChangePasswordRequest(String lastPassword, String newPassword, String newPasswordRepeat) {
        this.LastPassword = lastPassword;
        this.newPassword = newPassword;
        this.newPasswordRepeat = newPasswordRepeat;
    }
    public UserChangePasswordRequest() {
    }

}