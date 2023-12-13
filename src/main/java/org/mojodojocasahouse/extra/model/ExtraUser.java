package org.mojodojocasahouse.extra.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import org.mojodojocasahouse.extra.dto.UserRegistrationRequest;

@Entity
@Table(name = "USERS")
@Getter
public class ExtraUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotNull(message = "First name is mandatory")
    @Size(max = 100, message = "First name cannot exceed 100 characters in length")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "First name must not be left blank or contain special characters or numbers")
    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Setter
    @NotNull(message = "Last name is mandatory")
    @Size(max = 100, message = "Last name cannot exceed 100 characters in length")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "Last name must not be left blank or contain special characters or numbers")
    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Setter
    @NotBlank(message = "Email must not be left blank")
    @NotNull(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Setter
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_AUTHORITIES",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")}
    )
    private Set<Authority> authorities;

    public ExtraUser() {}

    public ExtraUser(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public ExtraUser(String firstName, String lastName, String email, String password, Set<Authority> authorities) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static ExtraUser from(UserRegistrationRequest userRegistrationDto, String encodedPassword){
        return new ExtraUser(
                userRegistrationDto.getFirstName(),
                userRegistrationDto.getLastName(),
                userRegistrationDto.getEmail(),
                encodedPassword
                );
    }
}