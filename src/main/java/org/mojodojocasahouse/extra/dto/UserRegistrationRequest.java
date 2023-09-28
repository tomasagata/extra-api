package org.mojodojocasahouse.extra.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.mojodojocasahouse.extra.validation.constraint.FieldsValueMatch;

@Data
@FieldsValueMatch(field = "password", fieldMatch = "passwordRepeat", message = "Passwords must match")
public class UserRegistrationRequest {

    @NotNull(message = "First name is mandatory")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "First name must not be left blank or contain special characters or numbers")
    private String firstName;

    @NotNull(message = "Last name is mandatory")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "Last name must not be left blank or contain special characters or numbers")
    private String lastName;

    @NotBlank(message = "Email must not be left blank")
    @NotNull(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Password is mandatory")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?]{8,}$", message = "Password must contain eight characters, one letter, one number and one of the following: @$!%*#?")
    private String password;

    @NotNull(message = "Repeating password is mandatory")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?]{8,}$", message = "Password must contain eight characters, one letter, one number and one of the following: @$!%*#?")
    private String passwordRepeat;

    public UserRegistrationRequest(String firstName, String lastName, String email, String password, String passwordRepeat) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.passwordRepeat = passwordRepeat;
    }

    public UserRegistrationRequest(){}

}
