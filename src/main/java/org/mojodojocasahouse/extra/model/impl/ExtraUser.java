package org.mojodojocasahouse.extra.model.impl;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.mojodojocasahouse.extra.model.UserEntity;

@Entity
@Table(name = "USERS", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
public class ExtraUser implements UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "FIRST_NAME")
    private String firstName;

    @Setter
    @Column(name = "LAST_NAME")
    private String lastName;

    @Setter
    @Column(name = "EMAIL", unique = true)
    private String email;

    @Setter
    @Column(name = "PASSWORD")
    private String password;


    public ExtraUser() {}

    public ExtraUser(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
    
}