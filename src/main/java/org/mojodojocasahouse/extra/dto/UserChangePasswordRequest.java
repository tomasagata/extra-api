package org.mojodojocasahouse.extra.dto;


import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserChangePasswordRequest {
    @NotNull(message = "Last Password is mandatory")
    @Pattern(regexp = "^([A-Za-z]|\\d|[@$!%*#?&])+$", message = "Password can only contain letters, numbers or the following: @$!%*#?")
    private String LastPassword;

    @NotNull(message = "New Password is mandatory")
    @Pattern(regexp = "^([A-Za-z]|\\d|[@$!%*#?&])+$", message = "Password can only contain letters, numbers or the following: @$!%*#?")
    private String newPassword;

    public UserChangePasswordRequest(String lastPassword, String newPassword) {
        this.LastPassword = lastPassword;
        this.newPassword = newPassword;
    }
    public UserChangePasswordRequest() {
    }

    
    


}